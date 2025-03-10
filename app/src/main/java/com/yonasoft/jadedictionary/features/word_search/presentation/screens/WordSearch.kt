package com.yonasoft.jadedictionary.features.word_search.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.core.words.presentation.components.CCWordColumn
import com.yonasoft.jadedictionary.features.word_search.presentation.components.WordSearchAppBar
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordSearchViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun WordSearch(
    navController: NavHostController,
    wordSearchViewModel: WordSearchViewModel = koinViewModel()
) {
    val inputTabs = listOf(
        ImageVector.vectorResource(R.drawable.baseline_keyboard_24),
        ImageVector.vectorResource(R.drawable.baseline_draw_24),
        ImageVector.vectorResource(R.drawable.outline_mic_24)
    )

    val focusRequester = wordSearchViewModel.focusRequester
    val focusManager = wordSearchViewModel.localFocusManager.current
    val keyboardController = wordSearchViewModel.localKeyboardController.current

    val searchQuery by wordSearchViewModel.searchQuery.collectAsStateWithLifecycle()
    val words by wordSearchViewModel.words.collectAsStateWithLifecycle()
    val selectedInputTab by wordSearchViewModel.selectedInputTab.collectAsStateWithLifecycle()

    LaunchedEffect(selectedInputTab) {
        focusRequester.requestFocus()
        kotlinx.coroutines.delay(100)
        when (selectedInputTab) {
            0 -> {
                keyboardController?.show()
            }
            1 -> {
                keyboardController?.hide()
            }
            2 -> {
                keyboardController?.hide()
            }
        }

    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            WordSearchAppBar(
                navigateUp = {
                    navController.navigateUp()
                },
                searchQuery = searchQuery,
                onCancel = {
                    if (searchQuery.isEmpty()) {
                        focusManager.clearFocus()
                    } else {
                        wordSearchViewModel.updateSearchQuery("")
                    }
                },
                onValueChange = { newQuery ->
                    wordSearchViewModel.updateSearchQuery(newQuery)
                },
                focusRequester = focusRequester
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            TabRow(
                modifier = Modifier.padding(8.dp),
                selectedTabIndex = selectedInputTab,
                containerColor = Color.Black,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedInputTab]),
                        color = Color.White
                    )
                }
            ) {
                inputTabs.forEachIndexed { index, icon ->
                    Tab(
                        selectedContentColor = Color.White,
                        content = {
                            Icon(
                                imageVector = icon,
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(28.dp)
                                    .background(color = Color.Black),
                            )
                        },
                        selected = selectedInputTab == index,
                        onClick = {
                            wordSearchViewModel.updateInputTab(index)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                Modifier
                    .fillMaxWidth(),
                state = rememberLazyListState()
            ) {
                if (searchQuery.isNotEmpty()) {
                    itemsIndexed(words) { _, word ->
                        CCWordColumn(word = word, onClick = {
                            navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
                        })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

