package com.yonasoft.jadedictionary.features.practice.presentation.screens.setup

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.PracticeRoutes
import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.HSKPracticeSetupViewModel
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import com.yonasoft.jadedictionary.features.word.presentation.components.HSKWordItemWithRemove
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HSKPracticeSetup(
    navController: NavHostController,
    viewModel: HSKPracticeSetupViewModel,
    modifier: Modifier = Modifier
) {
    var selectedVersion by rememberSaveable { mutableStateOf("HSK 3.0") }
    val versions = listOf("HSK 3.0", "HSK 2.0")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backgroundColor = Color(0xFF0A0A0A)

    // Get practice type for display
    val practiceTypeName = when (uiState.practiceType) {
        PracticeType.FLASH_CARDS -> "Flash Cards"
        PracticeType.MULTIPLE_CHOICE -> "Multiple Choice"
        PracticeType.LISTENING -> "Listening"
        else -> "Practice"
    }

    // Show error messages as snackbars
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // Show undo option when a word is removed
    LaunchedEffect(uiState.isUndoAvailable, uiState.lastRemovedWord) {
        if (uiState.isUndoAvailable && uiState.lastRemovedWord != null) {
            val wordName = uiState.lastRemovedWord?.displayText ?: "word"

            val result = snackbarHostState.showSnackbar(
                message = "Removed \"$wordName\" from practice",
                actionLabel = "UNDO"
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoWordRemoval()
            }
        }
    }

    // HSK Level selection dialog
    if (uiState.isHSKLevelModalOpen) {
        HSKLevelSelectionDialog(
            selectedVersion = selectedVersion,
            selectedLevels = uiState.selectedHSKLevels,
            onDismiss = { viewModel.closeHSKLevelModal() },
            onLevelsSelected = { selectedLevels ->
                viewModel.selectHSKLevels(selectedLevels)
            },
            viewModel = viewModel
        )
    }

    // Randomization count selector dialog
    if (uiState.isCountSelectorOpen) {
        RandomWordCountDialog(
            currentCount = uiState.randomWordCount,
            maxCount = uiState.availableWordCount,
            onDismiss = { viewModel.closeCountSelectorModal() },
            onCountSelected = { count ->
                viewModel.setRandomWordCount(count)
                viewModel.generateRandomWords()
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HSK $practiceTypeName Setup",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                },
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
        },
        floatingActionButton = {
            // Only show FAB when at least 4 words are selected
            if (uiState.selectedWords.size >= 4) {
                FloatingActionButton(
                    onClick = {
                        // Navigate to the next step
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Starting practice with ${uiState.selectedWords.size} words"
                            )
                        }

                        val wordIds = uiState.selectedWords.map { it.id }
                        val route = when (uiState.practiceType) {
                            PracticeType.FLASH_CARDS -> PracticeRoutes.FlashCardPractice.createRoute("HSK", wordIds)
                            PracticeType.MULTIPLE_CHOICE -> PracticeRoutes.MultipleChoicePractice.createRoute("HSK", wordIds)
                            PracticeType.LISTENING -> PracticeRoutes.ListeningPractice.createRoute("HSK", wordIds)
                            else -> PracticeRoutes.FlashCardPractice.createRoute("HSK", wordIds) // Default fallback
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Starting practice with ${uiState.selectedWords.size} words"
                            )
                        }

                        navController.navigate(route)
                    },
                    containerColor = CustomColor.GREEN01.color,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start Practice"
                    )
                }
            } else if (uiState.selectedWords.isNotEmpty()) {
                // Show disabled FAB with different color when words are selected but less than 4
                FloatingActionButton(
                    onClick = {
                        // Show message that more words are needed
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Please add at least ${4 - uiState.selectedWords.size} more word${if (4 - uiState.selectedWords.size > 1) "s" else ""}"
                            )
                        }
                    },
                    containerColor = Color.Gray.copy(alpha = 0.5f),
                    contentColor = Color.White.copy(alpha = 0.7f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Need More Words"
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.selectedWords.isEmpty()) {
                // Empty state
                EmptyHSKPracticeState(
                    onLevelSelect = { viewModel.openHSKLevelModal() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Word list with action buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Selected levels info and action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${if (uiState.selectedHSKVersion == HSKVersion.NEW) "HSK 3.0" else "HSK 2.0"} Levels: ${
                                    uiState.selectedHSKLevels.joinToString(
                                        ", "
                                    ) { it.toString() }
                                }",
                                color = CustomColor.GREEN01.color,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${uiState.selectedWords.size}/${uiState.availableWordCount} words selected",
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )

                            // Show remaining words needed if less than 4
                            if (uiState.selectedWords.size < 4) {
                                Text(
                                    text = "Need ${4 - uiState.selectedWords.size} more word${if (4 - uiState.selectedWords.size > 1) "s" else ""} to continue",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    letterSpacing = 0.4.sp
                                )
                            }
                        }

                        Row {
                            // Change HSK Levels button
                            OutlinedButton(
                                onClick = { viewModel.openHSKLevelModal() },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = CustomColor.GREEN01.color
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = SolidColor(CustomColor.GREEN01.color)
                                )
                            ) {
                                Text("Change Levels", fontSize = 12.sp)
                            }

                            // Randomize button
                            Button(
                                onClick = { viewModel.openCountSelectorModal() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CustomColor.GREEN01.color,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Randomize", fontSize = 12.sp)
                            }
                        }
                    }

                    // Word list
                    WordList(
                        words = uiState.selectedWords,
                        onRemove = { viewModel.removeWord(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHSKPracticeState(
    onLevelSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = CustomColor.GREEN01.color.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No HSK Words Selected",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Select HSK levels to create your practice session",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Select HSK levels button
            Button(
                onClick = onLevelSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_school_24),
                    contentDescription = null,
                    tint = CustomColor.GREEN01.color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Select HSK Levels",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun WordList(
    words: List<HSKWord>,
    onRemove: (HSKWord) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = words,
            key = { it.id }
        ) { word ->
            HSKWordItemWithRemove(
                word = word,
                onRemove = {
                    onRemove(word)
                },
                onWordClick = {} // No-op for now
            )
        }

        // Add bottom padding for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HSKLevelSelectionDialog(
    selectedVersion: String,
    selectedLevels: List<Int>,
    onDismiss: () -> Unit,
    onLevelsSelected: (List<Int>) -> Unit,
    viewModel: HSKPracticeSetupViewModel
) {
    var currentVersion by remember { mutableStateOf(selectedVersion) }
    val versions = listOf("HSK 3.0", "HSK 2.0")

    val levels = (1..7).toList() // HSK levels 1-7 as per your enum
    val hsk2Levels = (1..6).toList() // HSK 2.0 only has levels 1-6

    val tempSelectedLevels = remember { mutableStateListOf<Int>().apply { addAll(selectedLevels) } }

    val isValid = tempSelectedLevels.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121212),
        titleContentColor = Color.White,
        textContentColor = Color.White.copy(alpha = 0.8f),
        title = {
            Text(
                text = "Select HSK Levels",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Choose one or more HSK levels to practice:",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Version selector tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    versions.forEach { version ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    currentVersion = version
                                    // Clear selections when changing versions
                                    tempSelectedLevels.clear()
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = version,
                                fontSize = 14.sp,
                                fontWeight = if (currentVersion == version) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentVersion == version) CustomColor.GREEN01.color else Color.White.copy(
                                    alpha = 0.7f
                                )
                            )

                            // Indicator for selected version
                            if (currentVersion == version) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .width(40.dp)
                                        .background(CustomColor.GREEN01.color)
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Show appropriate version levels based on selection
                    if (currentVersion == "HSK 3.0") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // HSK 3.0 Levels (1-6)
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "HSK 3.0 (Standard)",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = CustomColor.GREEN01.color,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                levels.filter { it <= 6 }.forEach { level ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Checkbox(
                                            checked = tempSelectedLevels.contains(level),
                                            onCheckedChange = {
                                                if (it) {
                                                    tempSelectedLevels.add(level)
                                                } else {
                                                    tempSelectedLevels.remove(level)
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = CustomColor.GREEN01.color,
                                                uncheckedColor = Color.White.copy(alpha = 0.6f)
                                            )
                                        )
                                        Text(
                                            text = "Level $level",
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // HSK 3.0 Advanced Level (7)
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "HSK 3.0 (Advanced)",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = CustomColor.GREEN01.color,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Only show level 7 as per the enum
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = tempSelectedLevels.contains(7),
                                        onCheckedChange = {
                                            if (it) {
                                                tempSelectedLevels.add(7)
                                            } else {
                                                tempSelectedLevels.remove(7)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = CustomColor.GREEN01.color,
                                            uncheckedColor = Color.White.copy(alpha = 0.6f)
                                        )
                                    )
                                    Text(
                                        text = "Level 7",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    } else {
                        // HSK 2.0 Levels (1-6)
                        Column(Modifier.fillMaxWidth()) {
                            Text(
                                text = "HSK 2.0 Levels",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = CustomColor.GREEN01.color,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Show in grid of 2 columns
                            for (rowIndex in 0 until (hsk2Levels.size + 1) / 2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (colIndex in 0..1) {
                                        val levelIndex = rowIndex * 2 + colIndex
                                        if (levelIndex < hsk2Levels.size) {
                                            val level = hsk2Levels[levelIndex]
                                            Box(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                ) {
                                                    Checkbox(
                                                        checked = tempSelectedLevels.contains(level),
                                                        onCheckedChange = {
                                                            if (it) {
                                                                tempSelectedLevels.add(level)
                                                            } else {
                                                                tempSelectedLevels.remove(level)
                                                            }
                                                        },
                                                        colors = CheckboxDefaults.colors(
                                                            checkedColor = CustomColor.GREEN01.color,
                                                            uncheckedColor = Color.White.copy(alpha = 0.6f)
                                                        )
                                                    )
                                                    Text(
                                                        text = "Level $level",
                                                        fontSize = 14.sp,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val version =
                        if (currentVersion == "HSK 3.0") HSKVersion.NEW else HSKVersion.OLD
                    viewModel.setHSKVersion(version)
                    onLevelsSelected(tempSelectedLevels.toList())
                    onDismiss()
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColor.GREEN01.color,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RandomWordCountDialog(
    currentCount: Int,
    maxCount: Int,
    onDismiss: () -> Unit,
    onCountSelected: (Int) -> Unit
) {
    var tempCount by remember {
        mutableStateOf(
            currentCount.coerceAtMost(maxCount).coerceAtLeast(4)
        )
    }

    val isValid = tempCount in 4..maxCount

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121212),
        titleContentColor = Color.White,
        textContentColor = Color.White.copy(alpha = 0.8f),
        title = {
            Text(
                text = "Select Number of Words",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Select how many random words to include (4-$maxCount):",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Word count slider
                Text(
                    text = "$tempCount words",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = CustomColor.GREEN01.color,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Slider(
                    value = tempCount.toFloat(),
                    onValueChange = { tempCount = it.toInt() },
                    valueRange = 4f..maxCount.toFloat(),
                    steps = (maxCount - 4),
                    colors = SliderDefaults.colors(
                        thumbColor = CustomColor.GREEN01.color,
                        activeTrackColor = CustomColor.GREEN01.color,
                        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Info text about randomization
                Text(
                    text = "Random words will be selected from your chosen HSK levels. At least 4 words are required.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCountSelected(tempCount)
                    onDismiss()
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColor.GREEN01.color,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text("Randomize")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Text("Cancel")
            }
        }
    )
}