package com.yonasoft.jadedictionary.features.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R

@Composable
fun JadeBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) // Larger corner radius
            .background(Color.Black), // Black background to ensure no transparency
        contentAlignment = Alignment.Center
    ) {
        // Background image
        Image(
            painter = painterResource(R.drawable.jade_background),
            contentDescription = "Jade Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Enhanced gradient overlay for better text visibility and aesthetic appeal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),  // Darker at top
                            Color.Black.copy(alpha = 0.2f),  // More transparent in middle
                            Color.Black.copy(alpha = 0.8f)   // Darker at bottom
                        )
                    )
                )
        )

        // Text content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Jade Dictionary",
                fontSize = 36.sp, // Larger title
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color.White,
                letterSpacing = (-0.5).sp, // Tighter letter spacing for modern look
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = "Learn Chinese characters with ease",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                letterSpacing = 0.4.sp, // Slightly looser for subtitle
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
