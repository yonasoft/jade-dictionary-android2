@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
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
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.CreateWordListDialog
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.WordListColumnItem
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListsViewModel

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
    val searchQuery = wordListsState.searchQuery
    val selectedTab = uiState.selectedTab
    val isLoading = wordListsState.isLoading
    val errorMessage = uiState.errorMessage


    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = listOf(
        "HSK 2",
        "HSK 3",
        "My Lists",
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

    Scaffold(
        containerColor = Color.Black,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    SearchTextField(
                        searchQuery = searchQuery,
                        onValueChange = { wordListsViewModel.updateSearchQuery(it) },
                    )
                },
                actions = {

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                ),
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            JadeTabRowAlternative(selectedIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        selectedContentColor = CustomColor.GREEN01.color,
                        unselectedContentColor = Color.White.copy(alpha = 0.7f),
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
                                            .width(120.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(CustomColor.GREEN01.color.copy(alpha = 0.15f))
                                    )
                                }

                                Text(
                                    text = text,
                                    fontSize = 16.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) CustomColor.GREEN01.color else Color.White.copy(
                                        alpha = 0.7f
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
            Spacer(modifier = Modifier.height(4.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {
                    when (page) {
                        0 -> { /* HSK 2 content */
                        }

                        1 -> { /* HSK 3.0 content */
                        }

                        2 -> {
                            MyLists(
                                wordList = myWordLists,
                                onClick = { wordListId ->
                                    // Navigate to word list detail
                                    navController.navigate(
                                        WordListRoutes.WordListDetail.createRoute(
                                            wordListId
                                        )
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

@Composable
fun MyLists(
    wordList: List<CCWordList>,
    onClick: (Long) -> Unit,
    onCreateNewList: (title: String, description: String?) -> Unit,
    onDelete: (CCWordList) -> Unit
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
                WordListColumnItem(
                    wordList = wordList,
                    onClick = { id -> onClick(id) },
                    onDelete = { onDelete(it) }
                )
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
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF121212)
            )
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
                Text(
                    text = "Create New Word List",
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}