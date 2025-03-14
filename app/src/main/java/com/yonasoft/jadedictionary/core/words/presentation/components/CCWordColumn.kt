@file:OptIn(ExperimentalLayoutApi::class)

package com.yonasoft.jadedictionary.core.words.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord

@Composable
fun CCWordColumn(word: CCWord, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    // Use remember to ensure we always display the full word, not just the first character
    val fullDisplayText = remember(word.displayText) {
        word.displayText // Use the entire string, not word.displayText.take(1)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp) // Reduced vertical padding
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)) // Smaller elevation and corners
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121212)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Reduced elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Reduced padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Character and Pinyin
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(end = 8.dp)
            ) {
                // Chinese character
                Text(
                    text = fullDisplayText,
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, // Smaller font size
                    lineHeight = 24.sp,
                    overflow = TextOverflow.Visible
                )

                // Pinyin
                Text(
                    text = word.displayPinyin,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp, // Smaller font size
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Visible
                )
            }

            // Right side: Definition
            Text(
                text = word.definition ?: "",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp, // Smaller font size
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.7f)
            )
        }
    }
}