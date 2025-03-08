@file:OptIn(ExperimentalLayoutApi::class)

package com.yonasoft.jadedictionary.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.MainNavigation
import com.yonasoft.jadedictionary.features.home.presentation.components.HomeAppBar
import com.yonasoft.jadedictionary.features.home.presentation.components.JadeBanner
import com.yonasoft.jadedictionary.features.home.presentation.components.LinkDirector

@Composable
fun Home(navController: NavHostController) {
    Scaffold(
        topBar = {
            HomeAppBar {
                navController.navigate(MainNavigation.WordSearch.name)
            }
        },
        containerColor = Color.Black,
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            JadeBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp),
            )
            LinkDirector(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                icon = Icons.AutoMirrored.Filled.List,
                contentDescription = "Lists",
                label = "Lists",
                onClick = {},
            )
            ContextualFlowRow(
                itemCount = 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalArrangement = Arrangement.Center,
                maxLines = 2,
            ) {
            }
            LinkDirector(
                modifier = Modifier
                    .background(CustomColor.GRAY04.color)
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                icon = Icons.Default.Favorite,
                contentDescription = "Favorites",
                label = "Favorite",
                onClick = {},
            )
            LinkDirector(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                icon = ImageVector.vectorResource(id = R.drawable.baseline_videogame_asset_24),
                contentDescription = "Practice",
                label = "Practice",
                onClick = {},
            )
            LazyRow(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally,
                )
            ) {

            }
        }
    }
}

