@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.features.practice.presentation.screens.practice_modes

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.PracticeRoutes
import com.yonasoft.jadedictionary.features.practice.presentation.state.CardDisplayMode
import com.yonasoft.jadedictionary.features.practice.presentation.state.FlashCardState
import com.yonasoft.jadedictionary.features.practice.presentation.state.WordDifficulty
import com.yonasoft.jadedictionary.features.practice.presentation.state.WordResult
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.FlashCardPracticeViewModel
import com.yonasoft.jadedictionary.features.shared.presentation.components.openTTS
import com.yonasoft.jadedictionary.features.shared.presentation.components.rememberTextToSpeech
import com.yonasoft.jadedictionary.features.word.domain.Word
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import java.util.Locale

@Composable
fun FlashCardPractice(
    navController: NavHostController,
    viewModel: FlashCardPracticeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backgroundColor = Color(0xFF0A0A0A)

    // Handle TTS for Chinese pronunciation
    val tts = rememberTextToSpeech(Locale.CHINESE)
    var isSpeaking by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Flash Cards",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack(
                                PracticeRoutes.PracticeSelection.route,
                                false
                            )
                        },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { uiState.progress },
                color = CustomColor.GREEN01.color,
                trackColor = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress text
            Text(
                text = "${uiState.currentWordIndex}/${uiState.totalWords} words",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Main content
            if (uiState.isLoading) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading flash cards...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            } else if (uiState.isPracticeComplete) {
                // Results page
                FlashCardResults(
                    uiState = uiState,
                    onTabSelected = { viewModel.setResultsTabIndex(it) },
                    tts = tts.value!!,
                    setSpeaking = { isSpeaking = it }
                )
            } else {
                // Flash card practice
                FlashCardContent(
                    uiState = uiState,
                    onCardFlip = { viewModel.flipCard() },
                    onEasy = { viewModel.markCurrentWord(WordDifficulty.EASY) },
                    onMedium = { viewModel.markCurrentWord(WordDifficulty.MEDIUM) },
                    onHard = { viewModel.markCurrentWord(WordDifficulty.HARD) },
                    tts = tts.value,
                    setSpeaking = { isSpeaking = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FlashCardContent(
    uiState: FlashCardState,
    onCardFlip: () -> Unit,
    onEasy: () -> Unit,
    onMedium: () -> Unit,
    onHard: () -> Unit,
    tts: TextToSpeech?,
    setSpeaking: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentWord = uiState.getCurrentWord()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Flash card
        if (currentWord != null) {
            FlashCard(
                word = currentWord,
                isFlipped = uiState.isCardFlipped,
                onFlip = onCardFlip,
                displayMode = uiState.currentCardMode,
                tts = tts,
                setSpeaking = setSpeaking,
                modifier = Modifier.weight(1f)
            )

            // Difficulty buttons (only show when card is flipped)
            AnimatedVisibility(
                visible = uiState.isCardFlipped,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                DifficultyButtons(
                    onEasy = onEasy,
                    onMedium = onMedium,
                    onHard = onHard
                )
            }
        }
    }
}

@Composable
fun FlashCard(
    word: Word,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    displayMode: CardDisplayMode,
    tts: TextToSpeech?,
    setSpeaking: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ), label = "card-flip"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Front of card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                    alpha = if (rotation < 90f) 1f else 0f
                }
                .clickable { onFlip() },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display content based on the mode
                when (displayMode) {
                    CardDisplayMode.CHARACTERS -> {
                        when (word) {
                            is CCWord -> {
                                Text(
                                    text = word.displayText,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            is HSKWord -> {
                                Text(
                                    text = word.displayText,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    CardDisplayMode.PINYIN -> {
                        when (word) {
                            is CCWord -> {
                                Text(
                                    text = word.displayPinyin,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }

                            is HSKWord -> {
                                Text(
                                    text = word.pinyin ?: "",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    CardDisplayMode.DEFINITION -> {
                        when (word) {
                            is CCWord -> {
                                Text(
                                    text = word.definition ?: "",
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }

                            is HSKWord -> {
                                Text(
                                    text = word.displayDefinition,
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tap to flip",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Back of card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation - 180f
                    cameraDistance = 12f * density
                    alpha = if (rotation > 90f) 1f else 0f
                }
                .clickable { onFlip() },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                when (word) {
                    is CCWord -> {
                        Text(
                            text = word.displayText,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustomColor.GREEN01.color
                        )

                        Text(
                            text = word.displayPinyin,
                            fontSize = 24.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = {
                                word.simplified?.let {
                                    openTTS(tts ?: return@IconButton, it, setSpeaking)
                                }
                            },
                            modifier = Modifier
                                .background(
                                    color = CustomColor.GREEN01.color.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_volume_up_24),
                                contentDescription = "Pronounce word",
                                tint = CustomColor.GREEN01.color
                            )
                        }

                        Text(
                            text = word.definition ?: "",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }

                    is HSKWord -> {
                        Text(
                            text = word.displayText,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustomColor.GREEN01.color
                        )

                        Text(
                            text = word.pinyin ?: "",
                            fontSize = 24.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = {
                                openTTS(tts ?: return@IconButton, word.simplified, setSpeaking)
                            },
                            modifier = Modifier
                                .background(
                                    color = CustomColor.GREEN01.color.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_volume_up_24),
                                contentDescription = "Pronounce word",
                                tint = CustomColor.GREEN01.color
                            )
                        }

                        Text(
                            text = word.displayDefinition,
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )

                        if (word.displayClassifiers.isNotEmpty()) {
                            Text(
                                text = "Classifiers: ${word.displayClassifiers}",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (word.displayPartsOfSpeech.isNotEmpty()) {
                            Text(
                                text = "Parts of Speech: ${word.displayPartsOfSpeech}",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (word.hskNewLevel != null) {
                            Text(
                                text = "HSK 3.0 Level: ${word.hskNewLevel}",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (word.hskOldLevel != null) {
                            Text(
                                text = "HSK 2.0 Level: ${word.hskOldLevel}",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = "How well do you know this word?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}


@Composable
fun DifficultyButtons(
    onEasy: () -> Unit,
    onMedium: () -> Unit,
    onHard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Easy button
        ElevatedCard(
            onClick = onEasy,
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF1A5928),
                contentColor = Color.White
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Easy",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Easy",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Medium button
        ElevatedCard(
            onClick = onMedium,
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF73520D),
                contentColor = Color.White
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_radio_button_unchecked_24),
                    contentDescription = "Medium",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Medium",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Hard button
        ElevatedCard(
            onClick = onHard,
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF891919),
                contentColor = Color.White
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hard",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hard",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FlashCardResults(
    uiState: FlashCardState,
    onTabSelected: (Int) -> Unit,
    tts: TextToSpeech?,
    setSpeaking: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Practice Complete!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Results summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ResultSummaryItem(
                count = uiState.easyWords.size,
                label = "Easy",
                color = Color(0xFF1A5928)
            )

            ResultSummaryItem(
                count = uiState.mediumWords.size,
                label = "Medium",
                color = Color(0xFF73520D)
            )

            ResultSummaryItem(
                count = uiState.hardWords.size,
                label = "Hard",
                color = Color(0xFF891919)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab row for result categories
        TabRow(
            selectedTabIndex = uiState.resultsTabIndex,
            containerColor = Color(0xFF1A1A1A),
            contentColor = CustomColor.GREEN01.color
        ) {
            ResultTab(
                title = "Easy (${uiState.easyWords.size})",
                selected = uiState.resultsTabIndex == 0,
                onClick = { onTabSelected(0) }
            )

            ResultTab(
                title = "Medium (${uiState.mediumWords.size})",
                selected = uiState.resultsTabIndex == 1,
                onClick = { onTabSelected(1) }
            )

            ResultTab(
                title = "Hard (${uiState.hardWords.size})",
                selected = uiState.resultsTabIndex == 2,
                onClick = { onTabSelected(2) }
            )
        }

        // Pager with result lists
        val pagerState = rememberPagerState(
            initialPage = uiState.resultsTabIndex,
            pageCount = { 3 }
        )

        // Sync tab selection with pager
        LaunchedEffect(uiState.resultsTabIndex) {
            pagerState.animateScrollToPage(uiState.resultsTabIndex)
        }

        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage != uiState.resultsTabIndex) {
                onTabSelected(pagerState.currentPage)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> ResultsList(
                    results = uiState.easyWords,
                    tts = tts,
                    setSpeaking = setSpeaking
                )

                1 -> ResultsList(
                    results = uiState.mediumWords,
                    tts = tts,
                    setSpeaking = setSpeaking
                )

                2 -> ResultsList(
                    results = uiState.hardWords,
                    tts = tts,
                    setSpeaking = setSpeaking
                )
            }
        }
    }
}

@Composable
fun ResultTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selectedContentColor = CustomColor.GREEN01.color,
        unselectedContentColor = Color.White.copy(alpha = 0.7f)
    )
}

@Composable
fun ResultSummaryItem(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun ResultsList(
    results: List<WordResult>,
    tts: TextToSpeech?,
    setSpeaking: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No words in this category",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 16.sp
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(results.size) { index ->
                val result = results[index]
                ResultWordItem(
                    result = result,
                    tts = tts,
                    setSpeaking = setSpeaking
                )
            }
        }
    }
}


@Composable
fun ResultWordItem(
    result: WordResult,
    tts: TextToSpeech?,
    setSpeaking: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val word = result.word

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF1D1D1D),
            contentColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                when (word) {
                    is CCWord -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = word.displayText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CustomColor.GREEN01.color
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = word.displayPinyin,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = word.definition ?: "",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    is HSKWord -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = word.displayText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CustomColor.GREEN01.color
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = word.pinyin ?: "",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = word.displayDefinition,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "HSK ${if (word.hskNewLevel != null) "3.0: ${word.hskNewLevel}" else ""}" +
                                    if (word.hskOldLevel != null) " | HSK 2.0: ${word.hskOldLevel}" else "",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Play sound button
            IconButton(
                onClick = {
                    when (word) {
                        is CCWord -> word.simplified?.let {
                            openTTS(
                                tts ?: return@IconButton,
                                it,
                                setSpeaking
                            )
                        }

                        is HSKWord -> openTTS(
                            tts ?: return@IconButton,
                            word.simplified,
                            setSpeaking
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_volume_up_24),
                    contentDescription = "Pronounce word",
                    tint = CustomColor.GREEN01.color
                )
            }

            // Difficulty indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = when (result.difficulty) {
                            WordDifficulty.EASY -> Color(0xFF1A5928)
                            WordDifficulty.MEDIUM -> Color(0xFF73520D)
                            WordDifficulty.HARD -> Color(0xFF891919)
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}