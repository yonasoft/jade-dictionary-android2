package com.yonasoft.jadedictionary.features.word.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRowAlternative
import com.yonasoft.jadedictionary.features.shared.presentation.components.openTTS
import com.yonasoft.jadedictionary.features.shared.presentation.components.rememberTextToSpeech
import com.yonasoft.jadedictionary.features.word.data.local.sentences.Sentence
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.presentation.components.CCWordItem
import com.yonasoft.jadedictionary.features.word.presentation.components.SentenceColumn
import com.yonasoft.jadedictionary.features.word.presentation.components.WordDetailAppbar
import com.yonasoft.jadedictionary.features.word.presentation.viewmodels.WordDetailViewModel
import java.util.Locale

@Composable
fun WordDetail(
    navController: NavHostController,
    wordDetailViewModel: WordDetailViewModel
) {
    val tabs = listOf(
        "Chars",
        "Words",
        "Examples"
    )
    val selectedTab by wordDetailViewModel.tabIndex.collectAsStateWithLifecycle()
    val wordDetails by wordDetailViewModel.wordDetails.collectAsStateWithLifecycle()
    val characters by wordDetailViewModel.characters.collectAsStateWithLifecycle()
    val wordsOfWord by wordDetailViewModel.wordsOfWord.collectAsStateWithLifecycle()
    val sentences by wordDetailViewModel.sentences.collectAsStateWithLifecycle()
    val wordLists by wordDetailViewModel.wordLists.collectAsStateWithLifecycle()

    // Create a SnackbarHostState for snackbar display
    val snackbarHostState = remember { SnackbarHostState() }

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

    // Update background color to be darker for better contrast
    val backgroundColor = Color(0xFF0A0A0A)

    Scaffold(
        topBar = {
            WordDetailAppbar(
                title = wordDetails?.displayText ?: "",
                navigateUp = {
                    navController.navigateUp()
                },
                createNewWordList = { title, description ->
                    wordDetailViewModel.createNewWordList(title, description)
                },
                addWordToList = { selectedList ->
                    wordDetailViewModel.addWordToList(selectedList)
                },
                wordLists = wordLists,
                snackbarHostState = snackbarHostState
            )
        },
        // Set up the snackbar host
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { snackbarData ->
                // Custom snackbar appearance
                Snackbar(
                    containerColor = Color(0xFF303030),
                    contentColor = Color.White,
                    actionContentColor = CustomColor.GREEN01.color,
                    dismissActionContentColor = CustomColor.GREEN01.color,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = snackbarData.visuals.message,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        },
        containerColor = backgroundColor
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            // Word Detail Card - Enhanced with gradient
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent // Transparent for gradient
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Remove elevation for cleaner look
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0D47A1), // Deeper blue start
                                    Color(0xFF1565C0)  // Lighter blue end
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 240.dp)
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = wordDetails?.displayText ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                letterSpacing = (-0.5).sp, // Tighter letter spacing for characters
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = wordDetails?.displayPinyin ?: "",
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = wordDetails?.definition ?: "",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 18.sp,
                                lineHeight = 24.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                wordDetails?.simplified?.let { simplifiedText ->
                                    openTTS(
                                        tts = tts.value!!,
                                        text = simplifiedText,
                                        setSpeaking = { wordDetailViewModel.setIsSpeaking(it) }
                                    )
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .size(56.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_volume_up_24),
                                contentDescription = "Listen to sound icon",
                                Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Improved Tab Row
            JadeTabRowAlternative(
                selectedIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
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
                                            .width(100.dp) // Slightly narrower for cleaner look
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(CustomColor.GREEN01.color.copy(alpha = 0.15f))
                                    )
                                }

                                Text(
                                    text = text,
                                    fontSize = 16.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) CustomColor.GREEN01.color else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        },
                        selected = selectedTab == index,
                        onClick = {
                            wordDetailViewModel.updateSelectedTab(index)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content Pager
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
                        0 -> CharactersOfWord(characters, navController)
                        1 -> WordsOfWord(wordsOfWord, navController)
                        2 -> SentencesOfWord(sentences, onClick = { sentence ->
                            openTTS(
                                tts = tts.value!!,
                                text = sentence,
                                setSpeaking = { wordDetailViewModel.setIsSpeaking(it) })
                        })
                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Invalid Tab",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 20.sp
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
fun CharactersOfWord(characters: List<CCWord>, navController: NavHostController) {
    if (characters.isEmpty()) {
        EmptyStateMessage("No Characters Found")
        return
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(
            characters,
            key = { _, word -> word.id!! },
        ) { _, word ->
            CCWordItem(word = word, onClick = {
                navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
            })
        }
    }
}

@Composable
fun WordsOfWord(words: List<CCWord>, navController: NavHostController) {
    if (words.isEmpty()) {
        EmptyStateMessage("No Words Found")
        return
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(
            words,
            key = { i, _ -> i },
        ) { _, word ->
            CCWordItem( word = word, onClick = {
                navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
            })
        }
    }
}

@Composable
fun SentencesOfWord(sentences: List<Sentence>, onClick: (String) -> Unit) {
    if (sentences.isEmpty()) {
        EmptyStateMessage("No Sentences Found")
        return
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(
            sentences,
            key = { _, sentence -> sentence.id },
        ) { _, sentence ->
            SentenceColumn(sentence, onClick = { onClick(it) })
        }
    }
}

// Reusable empty state component with improved styling
@Composable
private fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Empty icon - you could add one here if desired

            Text(
                text = message,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp
            )
        }
    }
}