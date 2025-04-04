package com.yonasoft.jadedictionary.features.word.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.word.domain.sentences.Sentence
import com.yonasoft.jadedictionary.features.word.domain.utils.PinyinUtils

@Composable
fun SentenceColumn(
    sentence: Sentence,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A) // Slightly lighter for contrast
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Remove elevation for cleaner look
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(16.dp), // Increased padding
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = sentence.chineseSentence,
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.5).sp, // Tighter spacing for Chinese characters
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.padding(bottom = 6.dp) // More space after Chinese
                )
                Text(
                    text = PinyinUtils.toPinyinWithTones(sentence.chineseSentence),
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.padding(bottom = 10.dp) // More space after pinyin
                )
                Text(
                    text = sentence.englishTranslation,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }

            IconButton(
                onClick = { onClick(sentence.chineseSentence) },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(CustomColor.GREEN01.color.copy(alpha = 0.15f))
                    .size(44.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_volume_up_24),
                    contentDescription = "Listen to sound icon",
                    Modifier.size(24.dp),
                    tint = CustomColor.GREEN01.color
                )
            }
        }
    }
}


