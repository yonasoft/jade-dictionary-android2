@file:OptIn(ExperimentalLayoutApi::class)

package com.yonasoft.jadedictionary.core.words.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121212)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Character display in accent color circle
            Text(
                text = word.simplified!!.take(1), // Take first character for icon
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(CustomColor.GREEN01.color.copy(alpha = 0.8f))
                    .padding(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                // Character and pinyin with better spacing
                FlowRow(
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = word.displayText,
                        color = CustomColor.GREEN01.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = word.displayPinyin,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Visible
                    )
                }

                // Definition with improved styling
                Text(
                    text = word.definition ?: "",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}