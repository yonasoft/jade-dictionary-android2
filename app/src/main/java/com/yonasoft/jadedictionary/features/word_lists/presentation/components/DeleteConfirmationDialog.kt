package com.yonasoft.jadedictionary.features.word_lists.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList

@Composable
fun DeleteConfirmationDialog(
    wordList: WordList,
    onDismiss: () -> Unit,
    onConfirmDelete: (CCWordList) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp), // Larger corners for more modern look
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A0A0A) // Darker background for better contrast
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Remove elevation for cleaner look
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp), // Increased padding for better spacing
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete Word List",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp // Slight letter spacing for modern look
                )

                Spacer(modifier = Modifier.height(20.dp)) // Increased space

                Text(
                    text = "Are you sure you want to delete \"${wordList.title}\"? This action cannot be undone.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    letterSpacing = 0.3.sp,
                    lineHeight = 24.sp, // Better line height for readability
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirmDelete(wordList as CCWordList) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red.copy(alpha = 0.8f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp) // Slightly rounded corners for buttons
                    ) {
                        Text(
                            text = "Delete",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}