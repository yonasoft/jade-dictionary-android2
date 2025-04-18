package com.yonasoft.jadedictionary.features.word_lists.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.WordListRoutes
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRowAlternative
import com.yonasoft.jadedictionary.features.shared.presentation.components.SearchTextField
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.hsk.HSKWordList
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.CreateWordListDialog
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.HSKWordListItem
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.CCWordListItem
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordLists(
    navController: NavHostController,
    wordListsViewModel: WordListsViewModel,
    modifier: Modifier = Modifier
) {
    // Collect all state flows using the new bundled approach
    val wordListsState by wordListsViewModel.wordListsState.collectAsStateWithLifecycle()
    val uiState by wordListsViewModel.uiState.collectAsStateWithLifecycle()

    // Extract individual values from the states
    val myWordLists = wordListsState.myWordLists
    val hskOldWordLists = wordListsState.hskOldWordLists
    val hskNewWordLists = wordListsState.hskNewWordLists
    val searchQuery = wordListsState.searchQuery
    val selectedTab = uiState.selectedTab
    val isLoading = wordListsState.isLoading
    val errorMessage = uiState.errorMessage

    // Background color matching the app theme
    val backgroundColor = Color(0xFF0A0A0A)

    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = listOf(
        "HSK2",
        "HSK3",
        "Preset",
        "Custom",
    )

    val pagerState = rememberPagerState(initialPage = selectedTab) {
        tabs.size
    }

    LaunchedEffect(selectedTab) {
        pagerState.scrollToPage(selectedTab)
    }
    LaunchedEffect(pagerState.currentPage) {
        wordListsViewModel.updateInputTab(pagerState.currentPage)
    }

    // Show error in snackbar if present
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
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
                            contentDescription = "Navigate back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                title = {
                    SearchTextField(
                        searchQuery = searchQuery,
                        onValueChange = { wordListsViewModel.updateSearchQuery(it) },
                    )
                },
                actions = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF050505) // Darker app bar background
                ),
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            JadeTabRowAlternative(
                selectedIndex = selectedTab,
                tabs = tabs.size,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        selectedContentColor = CustomColor.GREEN01.color,
                        unselectedContentColor = Color.White.copy(alpha = 0.6f), // Slightly more subtle
                        content = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                            ) {
                                // Circle background for selected tab
                                if (selectedTab == index) {
                                    Box(
                                        modifier = Modifier
                                            .height(36.dp)
                                            .width(100.dp) // Slightly narrower for cleaner look
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(CustomColor.GREEN01.color.copy(alpha = 0.15f))
                                    )
                                }

                                Text(
                                    text = text,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.3.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) CustomColor.GREEN01.color else Color.White.copy(
                                        alpha = 0.6f
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        },
                        selected = selectedTab == index,
                        onClick = {
                            wordListsViewModel.updateInputTab(index)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->
                if (isLoading && (page == 0 || page == 1)) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CustomColor.GREEN01.color,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        when (page) {
                            0 -> { /* HSK 2.0 content */
                                HSKWordListsTab(
                                    wordLists = hskOldWordLists,
                                    onClick = { wordListId ->
                                        // Navigate to word list detail
                                        navController.navigate(
                                            WordListRoutes.WordListDetail.createRoute(wordListId)
                                        )
                                    }
                                )
                            }

                            1 -> { /* HSK 3.0 content */
                                HSKWordListsTab(
                                    wordLists = hskNewWordLists,
                                    onClick = { wordListId ->
                                        // Navigate to word list detail
                                        navController.navigate(
                                            WordListRoutes.WordListDetail.createRoute(wordListId)
                                        )
                                    }
                                )
                            }

                            2 -> { /* Preset content */
                                EmptyTabContent("Preset lists content coming soon")
                            }

                            3 -> {
                                MyLists(
                                    wordList = myWordLists,
                                    onClick = { wordListId ->
                                        // Navigate to word list detail
                                        navController.navigate(
                                            WordListRoutes.WordListDetail.createRoute(wordListId)
                                        )
                                    },
                                    onCreateNewList = { title, description ->
                                        wordListsViewModel.createNewWordList(title, description)
                                    },
                                    onDelete = {
                                        wordListsViewModel.deleteWordList(it)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HSKWordListsTab(
    wordLists: List<HSKWordList>,
    onClick: (Long) -> Unit
) {
    if (wordLists.isEmpty()) {
        EmptyTabContent("No HSK word lists available")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Sort HSK lists by level
        val sortedLists = wordLists.sortedBy { it.level.value }

        items(sortedLists, key = { it.id }) { wordList ->
            HSKWordListItem(
                wordList = wordList,
                onClick = onClick
            )
        }

        // Add bottom padding for better UX
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EmptyTabContent(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
fun MyLists(
    wordList: List<CCWordList>,
    onClick: (Long) -> Unit,
    onCreateNewList: (title: String, description: String?) -> Unit,
    onDelete: (WordList) -> Unit
) {
    val showDialog = rememberSaveable { mutableStateOf(false) }

    // Show dialog if state is true
    if (showDialog.value) {
        CreateWordListDialog(
            onDismiss = { showDialog.value = false },
            onConfirm = { title, description ->
                onCreateNewList(title, description)
                showDialog.value = false
            }
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        AddNewListButton(
            onClick = { showDialog.value = true }
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Existing word lists
            itemsIndexed(wordList, key = { _, wordList -> wordList.id ?: 0 }) { _, wordList ->
                CCWordListItem(
                    wordList = wordList,
                    onClick = { id -> onClick(id) },
                    onDelete = { onDelete(it) }
                )
            }

            // Add bottom padding for better UX
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AddNewListButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF121212)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Remove elevation for cleaner look
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new list",
                    tint = CustomColor.GREEN01.color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create New Word List",
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}