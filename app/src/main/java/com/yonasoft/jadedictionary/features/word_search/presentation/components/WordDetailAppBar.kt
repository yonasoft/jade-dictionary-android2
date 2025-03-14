@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.features.word_search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.CreateWordListDialog

@Composable
fun WordDetailAppbar(
    navigateUp: () -> Unit,
    title: String = "",
    createNewWordList: (title: String, description: String?) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showSnackbarTrigger by rememberSaveable { mutableStateOf<String?>(null) }

    // Move LaunchedEffect outside of the callback
    LaunchedEffect(showSnackbarTrigger) {
        showSnackbarTrigger?.let { title ->
            snackbarHostState.showSnackbar(
                message = "Word list \"$title\" created successfully",
                duration = SnackbarDuration.Short
            )
            // Reset after showing
            showSnackbarTrigger = null
        }
    }

    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = { navigateUp() },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                color = CustomColor.GREEN01.color,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        },
        actions = {
            // Add "Create New List" button
            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_playlist_add_24),
                    contentDescription = "Create new list",
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF121212)
        ),
        modifier = Modifier.shadow(8.dp)
    )

    // Create Word List Dialog
    if (showCreateDialog) {
        CreateWordListDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, description ->
                createNewWordList(title, description)
                showCreateDialog = false

                // Trigger the snackbar
                showSnackbarTrigger = title
            }
        )
    }
}