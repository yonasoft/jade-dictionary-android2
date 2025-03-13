@file:OptIn(ExperimentalLayoutApi::class)

package com.yonasoft.jadedictionary.features.handwriting.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Model for storing pen stroke data
data class PenStroke(
    val points: MutableList<Offset> = mutableListOf()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandwritingInputBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onCharacterDrawn: (List<Offset>) -> Unit,
    suggestedWords: List<String> = emptyList(),
    onSuggestionSelected: (String) -> Unit = {},
    isRecognizing: Boolean = false,
    resetCanvasSignal: Long = 0
) {
    // State for stroke collection
    val strokes = remember{ mutableStateListOf<PenStroke>() }
    var currentStroke by remember { mutableStateOf<PenStroke?>(null) }

    // For debouncing recognition requests
    val coroutineScope = rememberCoroutineScope()
    var recognitionJob by remember  { mutableStateOf<Job?>(null) }

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Get screen width to determine layout
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isWideScreen = screenWidth >= 600.dp

    // Hide bottom sheet when not visible
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            sheetState.hide()
        }
    }

    // Reset canvas when signal changes
    LaunchedEffect(resetCanvasSignal) {
        if (resetCanvasSignal > 0) {
            strokes.clear()
        }
    }

    // Function to trigger recognition with debounce
    val triggerRecognition = {
        recognitionJob?.cancel()
        recognitionJob = coroutineScope.launch {
            delay(300) // Debounce for 300ms
            val allPoints = strokes.flatMap { it.points }
            onCharacterDrawn(allPoints)
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.Black,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Handwriting Input",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Undo button
                        IconButton(
                            onClick = {
                                if (strokes.isNotEmpty()) {
                                    strokes.removeAt(strokes.lastIndex)
                                    triggerRecognition()
                                }
                            },
                            enabled = strokes.isNotEmpty()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_undo_24),
                                contentDescription = "Undo",
                                tint = if (strokes.isNotEmpty()) CustomColor.GREEN01.color else Color.Gray
                            )
                        }

                        // Clear button
                        IconButton(
                            onClick = {
                                strokes.clear()
                                onCharacterDrawn(emptyList()) // Signal to clear recognition data
                                recognitionJob?.cancel()
                            },
                            enabled = strokes.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = if (strokes.isNotEmpty()) CustomColor.GREEN01.color else Color.Gray
                            )
                        }

                        // Close button
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                if (isWideScreen) {
                    // Wide screen layout: Canvas on left, suggestions on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Drawing canvas (60% of width)
                        Box(
                            modifier = Modifier
                                .weight(0.6f)
                                .height(300.dp)
                        ) {
                            DrawingCanvas(
                                strokes = strokes,
                                currentStroke = currentStroke,
                                onStrokeStart = { offset ->
                                    currentStroke = PenStroke(mutableListOf(offset))
                                },
                                onStrokeUpdate = { offset ->
                                    currentStroke?.points?.add(offset)
                                },
                                onStrokeEnd = {
                                    currentStroke?.let { stroke ->
                                        if (stroke.points.size > 1) {
                                            strokes.add(stroke)
                                            triggerRecognition()
                                        }
                                    }
                                    currentStroke = null
                                }
                            )
                        }

                        // Suggestions panel (40% of width)
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .height(300.dp)
                                .padding(start = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SuggestionsPanel(
                                isRecognizing = isRecognizing,
                                suggestedWords = suggestedWords,
                                onSuggestionSelected = onSuggestionSelected
                            )
                        }
                    }
                } else {
                    // Narrow screen layout: Canvas on top, suggestions below
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Drawing canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            DrawingCanvas(
                                strokes = strokes,
                                currentStroke = currentStroke,
                                onStrokeStart = { offset ->
                                    currentStroke = PenStroke(mutableListOf(offset))
                                },
                                onStrokeUpdate = { offset ->
                                    currentStroke?.points?.add(offset)
                                },
                                onStrokeEnd = {
                                    currentStroke?.let { stroke ->
                                        if (stroke.points.size > 1) {
                                            strokes.add(stroke)
                                            triggerRecognition()
                                        }
                                    }
                                    currentStroke = null
                                }
                            )
                        }

                        // Suggestions area with fixed height
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SuggestionsPanel(
                                isRecognizing = isRecognizing,
                                suggestedWords = suggestedWords,
                                onSuggestionSelected = onSuggestionSelected
                            )
                        }
                    }
                }

                // Bottom margin for better UX
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DrawingCanvas(
    strokes: List<PenStroke>,
    currentStroke: PenStroke?,
    onStrokeStart: (Offset) -> Unit,
    onStrokeUpdate: (Offset) -> Unit,
    onStrokeEnd: () -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onStrokeStart(offset)
                    },
                    onDrag = { change, _ ->
                        onStrokeUpdate(change.position)
                    },
                    onDragEnd = {
                        onStrokeEnd()
                    }
                )
            }
    ) {
        // Draw completed strokes
        strokes.forEach { stroke ->
            if (stroke.points.size > 1) {
                val path = Path()
                path.moveTo(stroke.points.first().x, stroke.points.first().y)

                for (i in 1 until stroke.points.size) {
                    path.lineTo(stroke.points[i].x, stroke.points[i].y)
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        width = 5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // Draw current stroke
        currentStroke?.let { stroke ->
            if (stroke.points.size > 1) {
                val path = Path()
                path.moveTo(stroke.points.first().x, stroke.points.first().y)

                for (i in 1 until stroke.points.size) {
                    path.lineTo(stroke.points[i].x, stroke.points[i].y)
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        width = 5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Composable
private fun SuggestionsPanel(
    isRecognizing: Boolean,
    suggestedWords: List<String>,
    onSuggestionSelected: (String) -> Unit
) {
    when {
        isRecognizing -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = CustomColor.GREEN01.color,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Recognizing handwriting...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        suggestedWords.isNotEmpty() -> {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Character Suggestions",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Grid layout for characters
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 5
                ) {
                    suggestedWords.forEach { character ->
                        CharacterButton(
                            character = character,
                            onClick = { onSuggestionSelected(character) }
                        )
                    }
                }
            }
        }
        else -> {
            Text(
                text = "Draw to see character suggestions",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CharacterButton(
    character: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(CustomColor.GREEN01.color.copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = CustomColor.GREEN01.color.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = character,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.Normal
        )
    }
}