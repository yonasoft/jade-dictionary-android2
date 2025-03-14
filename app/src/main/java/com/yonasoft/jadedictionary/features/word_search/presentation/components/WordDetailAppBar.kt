package com.yonasoft.jadedictionary.features.word_search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.CreateWordListDialog
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.WordListSelectionDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailAppbar(
    title: String,
    navigateUp: () -> Unit,
    createNewWordList: (title: String, description: String?) -> Unit,
    addWordToList: (CCWordList) -> Unit,
    wordLists: List<CCWordList>,
    snackbarHostState: SnackbarHostState
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showWordListSelectionDialog by remember { mutableStateOf(false) }
    var showSnackbarTrigger by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Show create dialog if state is true
    if (showCreateDialog) {
        CreateWordListDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { newTitle, description ->
                createNewWordList(newTitle, description)
                showCreateDialog = false

                // Show confirmation
                scope.launch {
                    snackbarHostState.showSnackbar("Created new word list: $newTitle")
                }
            }
        )
    }

    // Show word list selection dialog
    if (showWordListSelectionDialog) {
        WordListSelectionDialog(
            wordLists = wordLists,
            onDismiss = { showWordListSelectionDialog = false },
            onWordListSelected = { selectedList ->
                addWordToList(selectedList)

                // Show confirmation
                scope.launch {
                    snackbarHostState.showSnackbar("Added \"$title\" to ${selectedList.title}")
                }
            }
        )
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 22.sp,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = Color.White
                )
            }
        },
        actions = {
            // Add to list button
            IconButton(
                onClick = { showWordListSelectionDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to word list",
                    tint = CustomColor.GREEN01.color
                )
            }
            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A)).size(40.dp)

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
        )
    )

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