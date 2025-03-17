package com.yonasoft.jadedictionary.features.word.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord


@Composable
fun HSKWordItem(
    word: HSKWord,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {}
) {
    // Use remember to ensure we always display the full word
    val fullDisplayText = remember(word.displayText) {
        word.displayText
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121212)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left column: Chinese character and pinyin
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(end = 12.dp)
            ) {
                // Chinese character
                Text(
                    text = fullDisplayText,
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    overflow = TextOverflow.Visible,
                    letterSpacing = (-0.5).sp
                )

                // Pinyin
                word.pinyin?.let {
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        overflow = TextOverflow.Visible,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            // Right column: Definition and HSK info
            Column(
                modifier = Modifier.weight(0.7f)
            ) {
                // Definition
                Text(
                    text = word.displayDefinition,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // HSK level info moved here
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_school_24),
                        contentDescription = "HSK Word",
                        tint = CustomColor.GREEN01.color,
                        modifier = Modifier
                            .size(16.dp)
                            .alpha(0.8f)
                            .padding(end = 4.dp)
                    )

                    // First show HSK 3.0 level (can include advanced levels 7-9)
                    word.hskNewLevel?.let {
                        val level = it.toString()
                        // Check if it's a higher level (7-9)
                        val displayText = if (it >= 7) {
                            "HSK3: 7-9"
                        } else {
                            "HSK3: $level"
                        }

                        Text(
                            text = displayText,
                            color = CustomColor.GREEN01.color.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }

                    // Then show HSK 2.0 level if available
                    word.hskOldLevel?.let {
                        Text(
                            text = "HSK2: $it",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            // Action buttons
            actions()
        }
    }
}

