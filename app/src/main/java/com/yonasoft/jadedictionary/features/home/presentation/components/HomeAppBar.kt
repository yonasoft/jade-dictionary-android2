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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor

@Composable
fun HomeAppBar(onClickSearch: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Image(
                painter =
                painterResource(R.drawable.jade_icon),
                contentDescription = "Jade Icon",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .width(48.dp)
                    .height(48.dp),
            )
        },
        title = {
            HomeSearchBar {
                onClickSearch()
            }
        },
        actions = {
//            IconButton(onClick = {}) {
//                Icon(
//                    imageVector = Icons.Default.MoreVert,
//                    contentDescription = "More menu",
//                    modifier = Modifier.size(28.dp),
//                )
//            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CustomColor.GRAY01.color
        ),
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
            .height(56.dp) // Set height for better visibility
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp)) // ✅ Use RoundedCornerShape
            .background(color = CustomColor.GRAY02.color)
            .clickable { onClickSearch() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            Modifier
                .size(28.dp)
                .padding(start = 2.dp),
        )
    }
}