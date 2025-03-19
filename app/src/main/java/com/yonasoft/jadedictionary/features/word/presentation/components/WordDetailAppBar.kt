package com.yonasoft.jadedictionary.features.word.presentation.components

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    createNewWordList: ((title: String, description: String?) -> Unit)? = null,
    addWordToList: ((CCWordList) -> Unit)? = null,
    wordLists: List<CCWordList>,
    snackbarHostState: SnackbarHostState
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showWordListSelectionDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Show create dialog if state is true
    if (showCreateDialog) {
        CreateWordListDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { newTitle, description ->
                createNewWordList?.let { it(newTitle, description) }
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
                addWordToList?.let { it(selectedList) }

                // Show confirmation
                scope.launch {
                    snackbarHostState.showSnackbar("Added \"$title\" to ${selectedList.title}")
                }
            }
        )
    }

    // Darker app bar color for better contrast
    val appBarColor = Color(0xFF050505)

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
                    fontSize = 20.sp, // Slightly smaller for cleaner look
                    letterSpacing = (-0.5).sp, // Tighter letter spacing for modern feel
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        actions = {
            // Add to list button
            addWordToList?.let {
                IconButton(
                    onClick = { showWordListSelectionDialog = true },
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to word list",
                        tint = CustomColor.GREEN01.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            createNewWordList?.let {
                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_playlist_add_24),
                        contentDescription = "Create new list",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appBarColor
        )
    )
}