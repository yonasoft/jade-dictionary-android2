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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord

@Composable
fun CCWordColumn(word: CCWord, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp, max = 100.dp)
            .background(Color.Black)
            .padding(10.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            FlowRow(maxLines = 2) {
                Text(
                    text = word.displayText,
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Visible
                )
                Text(
                    text = word.displayPinyin,
                    color = Color.White,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Visible
                )
            }
            Text(
                text = word.definition ?: "",
                color = Color.White,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}