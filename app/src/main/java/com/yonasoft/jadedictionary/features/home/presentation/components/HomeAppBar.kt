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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor

@Composable
fun HomeAppBar(onClickSearch: () -> Unit) {
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
                    .shadow(4.dp, CircleShape)
            )
        },
        title = {
            HomeSearchBar {
                onClickSearch()
            }
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CustomColor.GRAY01.color
        ),
        modifier = Modifier.shadow(8.dp)
    )
}

@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    onClickSearch: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color = CustomColor.GRAY02.color)
            .clickable { onClickSearch() }
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(24.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )

        Text(
            text = "Search for words...",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
        )
    }
}
