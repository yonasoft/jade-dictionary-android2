package com.yonasoft.jadedictionary.features.word_search.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor

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
    onSuggestionSelected: (String) -> Unit = {}
) {
    // State for stroke collection
    val strokes = remember { mutableStateListOf<PenStroke>() }
    var currentStroke by remember { mutableStateOf<PenStroke?>(null) }

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Hide bottom sheet when not visible
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            sheetState.hide()
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

                // Drawing canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentStroke = PenStroke(mutableListOf(offset))
                                },
                                onDrag = { change, _ ->
                                    val offset = change.position
                                    currentStroke?.points?.add(offset)
                                },
                                onDragEnd = {
                                    currentStroke?.let { stroke ->
                                        if (stroke.points.size > 1) {
                                            strokes.add(stroke)

                                            // Send all points for recognition
                                            val allPoints = mutableListOf<Offset>()
                                            strokes.forEach { s ->
                                                allPoints.addAll(s.points)
                                            }
                                            onCharacterDrawn(allPoints)
                                        }
                                    }
                                    currentStroke = null
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

                // Suggestions area
                if (suggestedWords.isNotEmpty()) {
                    Text(
                        text = "Suggestions",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestedWords.forEach { word ->
                            androidx.compose.material3.SuggestionChip(
                                onClick = { onSuggestionSelected(word) },
                                label = { Text(word) },
                                colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CustomColor.GREEN01.color.copy(alpha = 0.2f),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                } else {
                    // Placeholder for suggestions area
                    Spacer(modifier = Modifier.height(60.dp))
                    Text(
                        text = "Draw to see suggestions",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Bottom margin for better UX
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}