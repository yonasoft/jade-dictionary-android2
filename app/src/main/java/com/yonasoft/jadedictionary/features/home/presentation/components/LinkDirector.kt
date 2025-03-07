package com.yonasoft.jadedictionary.features.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun LinkDirector(label:String, onClick:()->Unit){
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .height(50.dp)
            .padding(5.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label.uppercase(Locale.getDefault()), color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Right Arrow",
            tint = Color.White,
            modifier = Modifier.size(45.dp),
        )
    }
}