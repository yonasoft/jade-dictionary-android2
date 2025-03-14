package com.yonasoft.jadedictionary.features.word_lists.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRow
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListsViewModel

@Composable
fun WordLists(navController: NavHostController, wordListsViewModel: WordListsViewModel, modifier: Modifier = Modifier) {

    val tabs = listOf(
        "HSK 2",
        "HSK 3.0",
//        "Premade",
        "My Lists",
    )

    val focusRequester = wordListsViewModel.focusRequester
    val focusManager = wordListsViewModel.localFocusManager.current
    val keyboardController = wordListsViewModel.localKeyboardController.current

    val searchQuery by wordListsViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by wordListsViewModel.selectedTab.collectAsStateWithLifecycle()

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
        topBar = {

        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            JadeTabRow(selectedIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        selectedContentColor = CustomColor.GREEN01.color,
                        content = {
                            Text(
                                text,
                                fontSize = 20.sp,
                                color = if (selectedTab == index) CustomColor.GREEN01.color else Color.White
                            )
                        },
                        selected = selectedTab == index,
                        onClick = {
                            wordListsViewModel.updateInputTab(index)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { _ ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {
                    when (selectedTab) {
                        0 ->{}
                        1 ->{}
                        2 ->{}
                        3 ->{}
                    }
                }
            }
        }
    }
}

