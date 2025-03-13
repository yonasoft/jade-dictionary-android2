package com.yonasoft.jadedictionary.features.word_search.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.yonasoft.jadedictionary.core.words.data.sentences.Sentence
import com.yonasoft.jadedictionary.core.words.presentation.components.CCWordColumn
import com.yonasoft.jadedictionary.core.words.presentation.components.SentenceColumn
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRow
import com.yonasoft.jadedictionary.features.shared.presentation.components.openTTS
import com.yonasoft.jadedictionary.features.shared.presentation.components.rememberTextToSpeech
import com.yonasoft.jadedictionary.features.word_search.presentation.components.WordDetailAppbar
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordDetailViewModel
import java.util.Locale

@Composable
fun WordDetail(
    navController: NavHostController,
    wordDetailViewModel: WordDetailViewModel
) {
    val tabs = listOf(
//        { Text("Strokes") },
        "Chars",
        "Words",
        "Examples"
    )
    val selectedTab by wordDetailViewModel.tabIndex.collectAsStateWithLifecycle()
    val wordDetails by wordDetailViewModel.wordDetails.collectAsStateWithLifecycle()
    val characters by wordDetailViewModel.characters.collectAsStateWithLifecycle()
    val wordsOfWord by wordDetailViewModel.wordsOfWord.collectAsStateWithLifecycle()
    val sentences by wordDetailViewModel.sentences.collectAsStateWithLifecycle()

    val tts = rememberTextToSpeech(Locale.CHINESE)
    val pagerState = rememberPagerState(initialPage = selectedTab) {
        tabs.size
    }
    LaunchedEffect(selectedTab) {
        pagerState.scrollToPage(selectedTab)
    }
    LaunchedEffect(pagerState.currentPage) {
        wordDetailViewModel.updateSelectedTab(pagerState.currentPage)
    }

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
                    .heightIn(min = 100.dp, 240.dp),
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
                            SpanStyle(color = Color.White, fontSize = 20.sp)
                        ) {
                            append("\n" + wordDetails?.definition)
                        }
                    },
                    fontSize = 32.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                )

                IconButton(onClick = {
                    openTTS(
                        tts = tts.value!!,
                        text = wordDetails!!.simplified ?: "",
                        setSpeaking = { wordDetailViewModel.setIsSpeaking(it) })
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
                                fontSize = 20.sp,
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
                        0 -> CharactersOfWord(characters, navController)
                        1 -> WordsOfWord(wordsOfWord, navController)
                        2 -> SentencesOfWord(sentences, onClick = { sentence ->
                            openTTS(
                                tts = tts.value!!,
                                text = sentence,
                                setSpeaking = { wordDetailViewModel.setIsSpeaking(it) })
                        })

                        else -> Text("Invalid Tab", color = Color.White)
                    }
                }
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
            key = { i, _ -> i },
        ) { _, word ->
            CCWordColumn(word = word, onClick = {
                navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
            })
            HorizontalDivider()
        }
    }
}

@Composable
fun SentencesOfWord(sentences: List<Sentence>, onClick: (String) -> Unit) {
    if (sentences.isEmpty()) {
        return Text(
            "No Sentences Found",
            color = Color.White,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
        )
    }
    LazyColumn(
        Modifier
            .fillMaxWidth(),
        state = rememberLazyListState()
    ) {
        itemsIndexed(
            sentences,
            key = { _, sentence -> sentence.id },
        ) { _, sentence ->
            SentenceColumn(sentence, onClick = { onClick(it) })
            HorizontalDivider()
        }
    }
}
