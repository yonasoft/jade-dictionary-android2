package com.yonasoft.jadedictionary.features.practice.presentation.screens.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.PracticeRoutes
import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType
import com.yonasoft.jadedictionary.features.practice.presentation.components.WordSearchModal
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.CCPracticeSetupViewModel
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.presentation.components.CCWordItemWithRemove
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.WordListSelectionDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CCPracticeSetup(
    navController: NavHostController,
    viewModel: CCPracticeSetupViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backgroundColor = Color(0xFF0A0A0A)

    // Get practice type for display
    val practiceTypeName = when (uiState.practiceType) {
        PracticeType.FLASH_CARDS -> "Flash Cards"
        PracticeType.MULTIPLE_CHOICE -> "Multiple Choice"
        PracticeType.LISTENING -> "Listening"
        else -> "Practice"
    }

    // Show error messages as snackbars
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // Show undo option when a word is removed
    LaunchedEffect(uiState.isUndoAvailable, uiState.lastRemovedWord) {
        if (uiState.isUndoAvailable && uiState.lastRemovedWord != null) {
            val wordName = uiState.lastRemovedWord?.displayText ?: "word"

            val result = snackbarHostState.showSnackbar(
                message = "Removed \"$wordName\" from practice",
                actionLabel = "UNDO"
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoWordRemoval()
            }
        }
    }

    // Word list selection dialog
    if (uiState.isWordListModalOpen) {
        WordListSelectionDialog(
            wordLists = viewModel.getWordLists(),
            onDismiss = { viewModel.closeWordListModal() },
            onWordListSelected = { selectedList ->
                viewModel.addWordsFromList(selectedList)
            },
            title = "Add from Word Lists"
        )
    }

    // Word search modal sheet
    WordSearchModal(
        isVisible = uiState.isSearchModalOpen,
        searchQuery = uiState.searchQuery,
        searchResults = uiState.searchResults,
        isLoading = uiState.isLoading,
        onDismiss = { viewModel.closeSearchModal() },
        onQueryChange = {
            viewModel.searchWords(it)
        },
        onAddWord = { word ->
            viewModel.addWord(word)
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Added \"${word.displayText}\" to practice"
                )
            }
        }
    )

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$practiceTypeName Setup",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF050505)
                )
            )
        },
        floatingActionButton = {
            // Only show FAB when at least 4 words are selected
            if (uiState.selectedWords.size >= 4) {
                FloatingActionButton(
                    onClick = {
                        // Navigate to the next step
                        // In a real implementation, this would save the setup and proceed
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Starting practice with ${uiState.selectedWords.size} words"
                            )
                        }

                        val wordIds = uiState.selectedWords.mapNotNull { it.id }
                        val route = when (uiState.practiceType) {
                            PracticeType.FLASH_CARDS -> PracticeRoutes.FlashCardPractice.createRoute("CC", wordIds)
                            PracticeType.MULTIPLE_CHOICE -> PracticeRoutes.MultipleChoicePractice.createRoute("CC", wordIds)
                            PracticeType.LISTENING -> PracticeRoutes.ListeningPractice.createRoute("CC", wordIds)
                            else -> PracticeRoutes.FlashCardPractice.createRoute("CC", wordIds) // Default fallback
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Starting practice with ${uiState.selectedWords.size} words"
                            )
                        }

                        navController.navigate(route)
                    },
                    containerColor = CustomColor.GREEN01.color,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start Practice"
                    )
                }
            } else if (uiState.selectedWords.isNotEmpty()) {
                // Show disabled FAB with different color when words are selected but less than 4
                FloatingActionButton(
                    onClick = {
                        // Show message that more words are needed
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Please add at least ${4 - uiState.selectedWords.size} more word${if (4 - uiState.selectedWords.size > 1) "s" else ""}"
                            )
                        }
                    },
                    containerColor = Color.Gray.copy(alpha = 0.5f),
                    contentColor = Color.White.copy(alpha = 0.7f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Need More Words"
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.selectedWords.isEmpty()) {
                // Empty state
                EmptyPracticeState(
                    onSearchClick = { viewModel.openSearchModal() },
                    onWordListClick = { viewModel.openWordListModal() }
                )
            } else {
                // Word list with action buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Word count and action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${uiState.selectedWords.size} words selected",
                                color = CustomColor.GREEN01.color,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )

                            // Show remaining words needed if less than 4
                            if (uiState.selectedWords.size < 4) {
                                Text(
                                    text = "Need ${4 - uiState.selectedWords.size} more word${if (4 - uiState.selectedWords.size > 1) "s" else ""} to continue",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    letterSpacing = 0.4.sp
                                )
                            }
                        }

                        Row {
                            // Search button
                            IconButton(
                                onClick = { viewModel.openSearchModal() },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search for words",
                                    tint = CustomColor.GREEN01.color
                                )
                            }

                            // Word list button
                            IconButton(
                                onClick = { viewModel.openWordListModal() },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.outline_playlist_add_24),
                                    contentDescription = "Add from word lists",
                                    tint = CustomColor.GREEN01.color
                                )
                            }
                        }
                    }

                    // Word list
                    WordList(
                        words = uiState.selectedWords,
                        onRemove = { viewModel.removeWord(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyPracticeState(
    onSearchClick: () -> Unit,
    onWordListClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = CustomColor.GREEN01.color.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Words Added Yet",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Add words to create your practice session",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Add word buttons
            Button(
                onClick = onSearchClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = CustomColor.GREEN01.color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Search for Words",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onWordListClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_view_list_24),
                    contentDescription = null,
                    tint = CustomColor.GREEN01.color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Add from Word Lists",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun WordList(
    words: List<CCWord>,
    onRemove: (CCWord) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = words,
            key = { it.id ?: 0 }
        ) { word ->
            CCWordItemWithRemove(
                word = word,
                onRemove = {
                    onRemove(word)
                },
                onWordClick = {},
            )
        }

        // Add bottom padding for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

