package com.yonasoft.jadedictionary.features.word_lists.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.shared.presentation.components.SearchTextField
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import com.yonasoft.jadedictionary.features.word.presentation.components.CCWordItemWithRemove
import com.yonasoft.jadedictionary.features.word.presentation.components.HSKWordItem
import com.yonasoft.jadedictionary.features.word_lists.domain.hsk.HSKWordList
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListDetailScreen(
    navController: NavController,
    viewModel: WordListDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Background color matching the app theme
    val backgroundColor = Color(0xFF0A0A0A)

    // Show error messages in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    // Show undo option when a word is removed (only for custom lists)
    LaunchedEffect(uiState.isUndoAvailable, uiState.lastRemovedWord) {
        if (uiState.isUndoAvailable && uiState.lastRemovedWord != null) {
            val wordName = when (val word = uiState.lastRemovedWord) {
                is CCWord -> word.displayText
                is HSKWord -> word.displayText
                else -> "word"
            }

            val result = snackbarHostState.showSnackbar(
                message = "Removed \"$wordName\" from list",
                actionLabel = "UNDO",
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoWordRemoval()
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White,
                    actionColor = CustomColor.GREEN01.color,
                    snackbarData = data,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditing) "Edit Word List" else uiState.wordList?.title ?: "Word List",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp // Tighter letter spacing for titles
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
                actions = {
                    // Only show edit actions for editable lists
                    if (!uiState.isHSKList) {
                        if (uiState.isEditing) {
                            IconButton(
                                onClick = { viewModel.saveEdits() },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_save_24),
                                    contentDescription = "Save changes",
                                    tint = CustomColor.GREEN01.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.startEditing() },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit list details",
                                    tint = CustomColor.GREEN01.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF050505) // Darker app bar background
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CustomColor.GREEN01.color,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            uiState.wordList == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Word list not found",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Editing section for title and description (custom lists only)
                    AnimatedVisibility(
                        visible = uiState.isEditing && !uiState.isHSKList,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Title field
                            OutlinedTextField(
                                value = uiState.editTitle,
                                onValueChange = { viewModel.updateEditTitle(it) },
                                label = {
                                    Text(
                                        "Title",
                                        color = Color.White.copy(alpha = 0.7f),
                                        letterSpacing = 0.3.sp
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CustomColor.GREEN01.color,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = CustomColor.GREEN01.color
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description field
                            OutlinedTextField(
                                value = uiState.editDescription,
                                onValueChange = { viewModel.updateEditDescription(it) },
                                label = {
                                    Text(
                                        "Description",
                                        color = Color.White.copy(alpha = 0.7f),
                                        letterSpacing = 0.3.sp
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CustomColor.GREEN01.color,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = CustomColor.GREEN01.color
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Display section for title and description when not editing
                    AnimatedVisibility(
                        visible = !uiState.isEditing,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // Display description if available
                            if (uiState.wordList?.description?.isNotBlank() == true) {
                                Text(
                                    text = uiState.wordList?.description ?: "",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 16.sp,
                                    letterSpacing = 0.3.sp,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            Text(
                                text = "${uiState.wordList?.numberOfWords ?: 0} words",
                                color = CustomColor.GREEN01.color,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.3.sp
                            )

                            // HSK info if applicable
                            if (uiState.isHSKList && uiState.wordList is HSKWordList) {
                                val hskList = uiState.wordList as HSKWordList
                                val versionName = if (hskList.version == com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion.OLD) {
                                    "HSK 2.0"
                                } else {
                                    "HSK 3.0"
                                }

                                Text(
                                    text = "This is an official $versionName vocabulary list",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.3.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // Search field
                    SearchTextField(
                        searchQuery = uiState.searchQuery,
                        onValueChange = { viewModel.searchWords(it) },
                        onCancel = {viewModel.resetQuery()},
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Word list
                    if (uiState.filteredWords.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.words.isEmpty())
                                    if (uiState.isHSKList) "No words found for this HSK level."
                                    else "This list is empty. Add words from the search screen."
                                else
                                    "No matching words found",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 16.sp,
                                letterSpacing = 0.3.sp,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(
                                items = uiState.filteredWords,
                                key = {
                                    when (it) {
                                        is CCWord -> it.id ?: 0
                                        is HSKWord -> it.id
                                        else -> 0
                                    }
                                }
                            ) { word ->
                                when (word) {
                                    is CCWord -> {
                                        CCWordItemWithRemove(
                                            word = word,
                                            onRemove = {
                                                if (!uiState.isHSKList) viewModel.removeWord(word)
                                            },
                                            onWordClick = {
                                                // Navigate to word detail
                                                navController.navigate("cc_word_detail/${word.id}")
                                            },
                                            showRemoveButton = !uiState.isHSKList
                                        )
                                    }
                                    is HSKWord -> {
                                        HSKWordItem(
                                            word = word,
                                            onClick = {
                                                navController.navigate("hsk_word_detail/${word.id}")
                                            }
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

