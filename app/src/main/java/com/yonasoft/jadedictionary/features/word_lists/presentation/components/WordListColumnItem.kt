package com.yonasoft.jadedictionary.features.word_lists.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList

@Composable
fun WordListColumnItem(wordList: WordList, onClick: () -> Unit, actions: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp) // Reduced vertical padding
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            ) // Smaller elevation and corners
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
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Chinese character
                Text(
                    text = wordList.title,
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, // Smaller font size
                    lineHeight = 24.sp,
                    overflow = TextOverflow.Visible
                )

                // Right side: Definition
                Text(
                    text = wordList.description,
                    color = Color.LightGray,
                    fontSize = 14.sp, // Smaller font size
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            actions()
        }
    }
}