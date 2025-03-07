package com.yonasoft.jadedictionary.features.home.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.home.presentation.components.HomeAppBar
import com.yonasoft.jadedictionary.features.home.presentation.components.LinkDirector

@Composable
fun Home() {
    Scaffold(
        topBar = {
            HomeAppBar {
                Log.i("clicked", "clicked")
            }
        },
        containerColor = CustomColor.GRAY03.color,
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(padding)
                .scrollable(state = scrollState, orientation = Orientation.Vertical),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 125.dp, max = 150.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.jade_background),
                    contentDescription = "Jade Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

                Text(
                    text = "Jade Dictionary",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier
                        .background(color = Color.Gray.copy(alpha = 0.6f))
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                )

            }
            LinkDirector("Lists") { }
            LinkDirector("Practice") { }
        }
    }
}