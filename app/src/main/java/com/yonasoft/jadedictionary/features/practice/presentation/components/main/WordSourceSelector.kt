package com.yonasoft.jadedictionary.features.practice.presentation.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.practice.domain.models.shared.WordSource

@Composable
fun WordSourceSelector(
    selectedSource: WordSource,
    onSourceSelected: (WordSource) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // HSK Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedSource == WordSource.HSK)
                            CustomColor.GREEN01.color.copy(alpha = 0.2f)
                        else
                            Color.Transparent
                    )
                    .clickable { onSourceSelected(WordSource.HSK) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HSK",
                    color = if (selectedSource == WordSource.HSK)
                        CustomColor.GREEN01.color
                    else
                        Color.White.copy(alpha = 0.7f),
                    fontWeight = if (selectedSource == WordSource.HSK)
                        FontWeight.Bold
                    else
                        FontWeight.Normal,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Custom Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedSource == WordSource.CUSTOM)
                            CustomColor.GREEN01.color.copy(alpha = 0.2f)
                        else
                            Color.Transparent
                    )
                    .clickable { onSourceSelected(WordSource.CUSTOM) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Custom",
                    color = if (selectedSource == WordSource.CUSTOM)
                        CustomColor.GREEN01.color
                    else
                        Color.White.copy(alpha = 0.7f),
                    fontWeight = if (selectedSource == WordSource.CUSTOM)
                        FontWeight.Bold
                    else
                        FontWeight.Normal,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}