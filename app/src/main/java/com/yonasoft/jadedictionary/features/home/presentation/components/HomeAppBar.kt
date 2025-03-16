@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.features.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R

@Composable
fun HomeAppBar(onClickSearch: () -> Unit) {
    // Darker app bar color for better contrast
    val appBarColor = Color(0xFF050505)

    TopAppBar(
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.jade_icon),
                contentDescription = "Jade Icon",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
            )
        },
        title = {
            HomeSearchBar {
                onClickSearch()
            }
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appBarColor
        )
    )
}

@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    onClickSearch: () -> Unit
) {
    // Slightly lighter search bar color for subtle contrast with app bar
    val searchBarColor = Color(0xFF1A1A1A)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(23.dp)) // Half of height for perfect circle
            .background(color = searchBarColor)
            .clickable { onClickSearch() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(20.dp), // Smaller icon for minimalist look
            tint = Color.White.copy(alpha = 0.6f) // More subtle icon
        )

        Text(
            text = "Search for words...",
            color = Color.White.copy(alpha = 0.5f), // More subtle hint text
            fontSize = 15.sp, // Slightly smaller for minimalist feel
            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
        )
    }
}
