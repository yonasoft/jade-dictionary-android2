package com.yonasoft.jadedictionary.core.words.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.words.data.sentences.Sentence
import com.yonasoft.jadedictionary.core.words.utils.PinyinUtils

@Composable
fun SentenceColumn(
    sentence: Sentence,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp, max = 150.dp)
            .background(Color.Black)
            .padding(10.dp),
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
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                overflow = TextOverflow.Visible
            )
            Text(
                text = PinyinUtils.toPinyinWithTones(sentence.chineseSentence),
                color =  Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                overflow = TextOverflow.Visible
            )
            Text(
                text = sentence.englishTranslation,
                color = Color.White,
                fontSize = 14.sp,
            )
        }

        IconButton(onClick = {
            onClick(sentence.chineseSentence)
        }) {
            Icon(
                painter = painterResource(R.drawable.baseline_volume_up_24),
                contentDescription = "Listen to sound icon",
                Modifier.size(28.dp),
                tint = Color.White,
            )
        }
    }
}

