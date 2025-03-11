package com.yonasoft.jadedictionary.features.word_search.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.core.words.data.cc.CCWord
import com.yonasoft.jadedictionary.core.words.presentation.components.CCWordColumn
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRow
import com.yonasoft.jadedictionary.features.word_search.presentation.components.WordDetailAppbar
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WordDetail(
    navController: NavHostController,
    wordDetailViewModel: WordDetailViewModel = koinViewModel(),
) {
    val tabs = listOf(
//        { Text("Strokes") },
        "Characters",
        "Words",
        "Examples"
    )
    val selectedTab by wordDetailViewModel.tabIndex.collectAsStateWithLifecycle()
    val wordDetails by wordDetailViewModel.wordDetails.collectAsStateWithLifecycle()
    val characters by wordDetailViewModel.characters.collectAsStateWithLifecycle()
    val wordsOfWord by wordDetailViewModel.wordsOfWord.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            WordDetailAppbar(title = wordDetails?.displayText ?: "", navigateUp = {
                navController.navigateUp()
            })
        },
        containerColor = Color.Black
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, 160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = CustomColor.GREEN01.color,

                                )
                        ) {
                            append(wordDetails?.displayText ?: "")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color.White
                            )
                        ) {
                            append(wordDetails?.displayPinyin ?: "")
                        }
                        withStyle(
                            SpanStyle(color = Color.White, fontSize = 24.sp)
                        ) {
                            append("\n" + wordDetails?.definition)
                        }
                    },
                    fontSize = 32.sp,
                )

                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_volume_up_24),
                        contentDescription = "Listen to sound icon",
                        Modifier.size(36.dp),
                        tint = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            JadeTabRow(selectedIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        selectedContentColor = CustomColor.GREEN01.color,
                        content = {
                            Text(
                                text,
                                fontSize = 24.sp,
                                color = if (selectedTab == index) CustomColor.GREEN01.color else Color.White
                            )
                        },
                        selected = selectedTab == index,
                        onClick = {
                            wordDetailViewModel.updateSelectedTab(index)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            when (selectedTab) {
                0 -> CharactersOfWord(characters, navController)
                1 -> WordsOfWord(wordsOfWord, navController)
                2 -> Text("Examples Tab", color = Color.White)
                else -> Text("Invalid Tab", color = Color.White)
            }
        }
    }
}

@Composable
fun CharactersOfWord(characters: List<CCWord>, navController: NavHostController) {
    LazyColumn(
        Modifier
            .fillMaxWidth(),
        state = rememberLazyListState()
    ) {
        itemsIndexed(
            characters,
            key = { _, word -> word.id!! },
        ) { _, word ->
            CCWordColumn(word = word, onClick = {
                navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
            })
            HorizontalDivider()
        }
    }
}

@Composable
fun WordsOfWord(words: List<CCWord>, navController: NavHostController) {
    if (words.isEmpty()) {
        return Text(
            "No Words Found",
            color = Color.White,
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
        )
    }
    LazyColumn(
        Modifier
            .fillMaxWidth(),
        state = rememberLazyListState()
    ) {
        itemsIndexed(
            words,
            key = { _, word -> word.id!! },
        ) { _, word ->
            CCWordColumn(word = word, onClick = {
                navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
            })
            HorizontalDivider()
        }
    }
}

