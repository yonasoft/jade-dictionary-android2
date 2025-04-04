@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.PracticeRoutes
import com.yonasoft.jadedictionary.features.practice.presentation.state.ListeningMode
import com.yonasoft.jadedictionary.features.practice.presentation.state.ListeningQuestion
import com.yonasoft.jadedictionary.features.practice.presentation.state.ListeningState
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.ListeningPracticeViewModel
import com.yonasoft.jadedictionary.features.shared.presentation.components.openTTS
import com.yonasoft.jadedictionary.features.shared.presentation.components.rememberTextToSpeech
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * If not already defined in the project, we need this utility function
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val widthDp = configuration.screenWidthDp.dp
    val heightDp = configuration.screenHeightDp.dp

    return WindowSizeClass.calculateFromSize(DpSize(widthDp, heightDp))
}

@Composable
fun ListeningPractice(
    navController: NavHostController,
    viewModel: ListeningPracticeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val backgroundColor = Color(0xFF0A0A0A)
    val windowSizeClass = rememberWindowSizeClass()
    val scope = rememberCoroutineScope()

    // Handle TTS for Chinese pronunciation
    val tts = rememberTextToSpeech(Locale.CHINESE)
    var isSpeaking by remember { mutableStateOf(false) }
    var useSlow by remember { mutableStateOf(false) }

    // Auto-play the audio when a new question appears
    LaunchedEffect(uiState.currentQuestionIndex, useSlow) {
        if (!uiState.isLoading && !uiState.isPracticeComplete) {
            uiState.getCurrentQuestion()?.let { question ->
                playQuestionAudio(question, tts.value,
                    isPlaying = { viewModel.setAudioPlaying(it) },
                    setSpeaking = { isSpeaking = it },
                    useSlow = useSlow
                )
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Listening Practice",
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
                text = "${uiState.currentQuestionIndex + 1}/${uiState.totalQuestions} questions",
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
                        text = "Loading listening exercises...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            } else if (uiState.isPracticeComplete) {
                // Results page
                ListeningResults(
                    uiState = uiState,
                    tts = tts.value,
                    setSpeaking = { isSpeaking = it },
                    windowSizeClass = windowSizeClass,
                    onRetryMissedQuestions = { viewModel.retryMissedQuestions() },
                    onFinish = { navController.popBackStack(PracticeRoutes.PracticeSelection.route, false) }
                )
            } else {
                // Listening question practice
                ListeningContent(
                    uiState = uiState,
                    onOptionSelected = { viewModel.selectOption(it) },
                    onNextQuestion = { viewModel.moveToNextQuestion() },
                    onPlayAudio = {
                        uiState.getCurrentQuestion()?.let { question ->
                            scope.launch {
                                playQuestionAudio(question, tts.value,
                                    isPlaying = { viewModel.setAudioPlaying(it) },
                                    setSpeaking = { isSpeaking = it },
                                    useSlow = useSlow
                                )
                            }
                        }
                    },
                    onToggleSpeed = { useSlow = !useSlow },
                    useSlow = useSlow,
                    tts = tts.value,
                    isAudioPlaying = uiState.isAudioPlaying,
                    modifier = Modifier.weight(1f),
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}

suspend fun playQuestionAudio(
    question: ListeningQuestion,
    tts: TextToSpeech?,
    isPlaying: (Boolean) -> Unit,
    setSpeaking: (Boolean) -> Unit,
    useSlow: Boolean = false
) {
    if (tts == null) return

    isPlaying(true)
    setSpeaking(true)

    when (val word = question.word) {
        is CCWord -> {
            word.simplified?.let {
                val speechRate = if (useSlow) 0.5f else 1.0f
                tts.setSpeechRate(speechRate)
                openTTS(tts, it, setSpeaking)
                delay(100)  // Give a little time for TTS to start

                // Wait until TTS is done
                while (tts.isSpeaking) {
                    delay(100)
                }
            }
        }
        is HSKWord -> {
            val speechRate = if (useSlow) 0.5f else 1.0f
            tts.setSpeechRate(speechRate)
            openTTS(tts, word.simplified, setSpeaking)
            delay(100)  // Give a little time for TTS to start

            // Wait until TTS is done
            while (tts.isSpeaking) {
                delay(100)
            }
        }
    }

    // Reset speech rate to normal
    tts.setSpeechRate(1.0f)
    isPlaying(false)
}

@Composable
fun ListeningContent(
    uiState: ListeningState,
    onOptionSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPlayAudio: () -> Unit,
    onToggleSpeed: () -> Unit,
    useSlow: Boolean,
    tts: TextToSpeech?,
    isAudioPlaying: Boolean,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val isWideScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val isLandscape = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact &&
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    val currentQuestion = uiState.getCurrentQuestion()

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isWideScreen) 32.dp else 16.dp,
                    vertical = 16.dp
                )
                .verticalScroll(scrollState)
                // Add bottom padding to ensure space for the feedback/next button section
                .padding(bottom = if (uiState.isAnswerRevealed) 160.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Question card with audio controls
            if (currentQuestion != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isWideScreen) {
                                Modifier.widthIn(max = 700.dp).wrapContentWidth(Alignment.CenterHorizontally)
                            } else {
                                Modifier
                            }
                        )
                        .padding(vertical = 16.dp),
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
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Question title based on mode
                        Text(
                            text = when (currentQuestion.mode) {
                                ListeningMode.AUDIO_TO_CHARACTER -> "Listen and select the correct character"
                                ListeningMode.AUDIO_TO_PINYIN -> "Listen and select the correct pinyin"
                                ListeningMode.AUDIO_TO_DEFINITION -> "Listen and select the correct meaning"
                            },
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Audio control buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Play button
                            IconButton(
                                onClick = onPlayAudio,
                                enabled = !isAudioPlaying,
                                modifier = Modifier
                                    .background(
                                        color = CustomColor.GREEN01.color.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .size(80.dp)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_volume_up_24),
                                    contentDescription = "Play audio",
                                    tint = if (isAudioPlaying)
                                        CustomColor.GREEN01.color.copy(alpha = 0.5f)
                                    else
                                        CustomColor.GREEN01.color,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Speed control button
                            IconButton(
                                onClick = onToggleSpeed,
                                modifier = Modifier
                                    .background(
                                        color = if (useSlow)
                                            CustomColor.GREEN01.color.copy(alpha = 0.2f)
                                        else
                                            Color.Gray.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .size(48.dp)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_fast_forward_24),
                                    contentDescription = if (useSlow) "Normal speed" else "Slow speed",
                                    tint = if (useSlow)
                                        CustomColor.GREEN01.color
                                    else
                                        Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Speed indicator text
                        Text(
                            text = if (useSlow) "Slow speed" else "Normal speed",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Audio playing indicator
                        if (isAudioPlaying) {
                            Text(
                                text = "Playing audio...",
                                fontSize = 14.sp,
                                color = CustomColor.GREEN01.color
                            )
                        } else {
                            Text(
                                text = "Tap to listen",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Answer options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isWideScreen) {
                                Modifier.widthIn(max = 700.dp).wrapContentWidth(Alignment.CenterHorizontally)
                            } else {
                                Modifier
                            }
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Answer options
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = uiState.selectedOptionIndex == index
                        val isCorrect = uiState.isAnswerRevealed && index == currentQuestion.correctOptionIndex
                        val isIncorrect = uiState.isAnswerRevealed && isSelected && !isCorrect

                        ListeningOption(
                            option = option,
                            questionMode = currentQuestion.mode,
                            isSelected = isSelected,
                            isCorrect = isCorrect,
                            isIncorrect = isIncorrect,
                            isAnswerRevealed = uiState.isAnswerRevealed,
                            onOptionClick = { if (!uiState.isAnswerRevealed) onOptionSelected(index) }
                        )
                    }
                }
            }
        }

        // Next button or feedback - positioned at the bottom of the screen
        AnimatedVisibility(
            visible = uiState.isAnswerRevealed,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF0A0A0A)) // match background color
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = if (isWideScreen) 32.dp else 16.dp)
                    .fillMaxWidth()
            ) {
                // Feedback message
                val isCorrect = uiState.selectedOptionIndex == currentQuestion?.correctOptionIndex

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isCorrect) "Correct" else "Incorrect",
                        tint = if (isCorrect) Color(0xFF1A5928) else Color(0xFF891919),
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isCorrect) "Correct!" else "Incorrect",
                        color = if (isCorrect) Color(0xFF1A5928) else Color(0xFF891919),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Show correct answer if wrong
                if (!isCorrect) {
                    Text(
                        text = "The correct answer is:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.8f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A),
                            contentColor = Color(0xFF1A5928)
                        )
                    ) {
                        Text(
                            text = currentQuestion?.options?.get(currentQuestion.correctOptionIndex) ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Next button
                Button(
                    onClick = onNextQuestion,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomColor.GREEN01.color,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(
                        if (isWideScreen) 0.5f else 0.8f
                    ).then(
                        if (isWideScreen) Modifier.wrapContentWidth(Alignment.CenterHorizontally) else Modifier
                    )
                ) {
                    Text(
                        text = if (uiState.currentQuestionIndex < uiState.totalQuestions - 1)
                            "Next Question" else "See Results",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next"
                    )
                }
            }
        }
    }
}

@Composable
fun ListeningOption(
    option: String,
    questionMode: ListeningMode,
    isSelected: Boolean,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    isAnswerRevealed: Boolean,
    onOptionClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect -> Color(0xFF1A5928)
        isIncorrect -> Color(0xFF891919)
        isSelected -> CustomColor.GREEN01.color.copy(alpha = 0.3f)
        else -> Color(0xFF1A1A1A)
    }

    val borderColor = when {
        isSelected && !isAnswerRevealed -> CustomColor.GREEN01.color
        isCorrect -> Color(0xFF1A5928)
        isIncorrect -> Color(0xFF891919)
        else -> Color(0xFF2A2A2A)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isAnswerRevealed, onClick = onOptionClick)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected || isCorrect || isIncorrect) 4.dp else 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Answer content
            Text(
                text = option,
                fontSize = when (questionMode) {
                    ListeningMode.AUDIO_TO_PINYIN -> 18.sp
                    ListeningMode.AUDIO_TO_CHARACTER ->
                        if (option.length < 4) 20.sp else 18.sp
                    ListeningMode.AUDIO_TO_DEFINITION ->
                        if (option.length > 20) 14.sp else 16.sp
                },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Show check or x icon if answer is revealed
            if (isAnswerRevealed && (isCorrect || isIncorrect)) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ListeningResults(
    uiState: ListeningState,
    tts: TextToSpeech?,
    setSpeaking: (Boolean) -> Unit,
    windowSizeClass: WindowSizeClass,
    onRetryMissedQuestions: () -> Unit,
    onFinish: () -> Unit
) {
    val isWideScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isWideScreen) 32.dp else 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Practice Complete!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Score card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isWideScreen) {
                        Modifier.widthIn(max = 700.dp).wrapContentWidth(Alignment.CenterHorizontally)
                    } else {
                        Modifier
                    }
                ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Score
                val correctCount = uiState.results.count { it.isCorrect }
                val totalCount = uiState.results.size
                val scorePercentage = (correctCount.toFloat() / totalCount.toFloat() * 100).toInt()

                Text(
                    text = "$scorePercentage%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        scorePercentage >= 80 -> CustomColor.GREEN01.color
                        scorePercentage >= 60 -> Color(0xFFDAAA00)
                        else -> Color(0xFFE57373)
                    }
                )

                Text(
                    text = "$correctCount of $totalCount correct",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Progress bar
                LinearProgressIndicator(
                    progress = { correctCount.toFloat() / totalCount.toFloat() },
                    color = when {
                        scorePercentage >= 80 -> CustomColor.GREEN01.color
                        scorePercentage >= 60 -> Color(0xFFDAAA00)
                        else -> Color(0xFFE57373)
                    },
                    trackColor = Color.Gray.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                )

                // Summary stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correct answers",
                            tint = CustomColor.GREEN01.color,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "$correctCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustomColor.GREEN01.color
                        )
                        Text(
                            text = "Correct",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Incorrect answers",
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "${totalCount - correctCount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE57373)
                        )
                        Text(
                            text = "Incorrect",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Text(
                    text = "Keep practicing to improve your listening skills!",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Retry missed questions button (only if there are any)
            if (uiState.results.any { !it.isCorrect }) {
                Button(
                    onClick = onRetryMissedQuestions,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1A1A),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isWideScreen) {
                                Modifier.widthIn(max = 500.dp)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry missed questions",
                        tint = CustomColor.GREEN01.color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Retry Missed Questions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Finish button
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColor.GREEN01.color,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isWideScreen) {
                            Modifier.widthIn(max = 500.dp)
                        } else {
                            Modifier
                        }
                    )
            ) {
                Text(
                    text = "Finish Practice",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}