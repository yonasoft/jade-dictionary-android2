package com.yonasoft.jadedictionary.features.word_search.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.features.word_search.presentation.components.WordSearchAppBar


@Composable
fun WordSearch(navController: NavHostController) {
    val focusRequester = remember { FocusRequester() }
    val inputTabs = listOf(
        ImageVector.vectorResource(R.drawable.baseline_keyboard_24),
        ImageVector.vectorResource(R.drawable.baseline_draw_24),
        ImageVector.vectorResource(R.drawable.outline_mic_24)
    )
    var selectedInputTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            WordSearchAppBar(navigateUp = {
                navController.navigateUp()
            },
                focusRequester = focusRequester
            )
        }
    ) { paddingValue ->
        LazyColumn(
            Modifier
                .padding(paddingValue)
                .fillMaxWidth()
        ) {
            item {
                TabRow(
                    selectedTabIndex = selectedInputTab,
                    containerColor = Color.Black,
                    modifier = Modifier.padding(8.dp),
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
                                        .size(32.dp)
                                        .background(color = Color.Black),
                                )
                            },
                            selected = selectedInputTab == index,
                            onClick = {
                                selectedInputTab = index
                            }
                        )
                    }
                }

            }
        }
    }
}

