package com.yonasoft.jadedictionary.features.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.navigation.MainRoutes
import com.yonasoft.jadedictionary.features.home.presentation.components.HomeAppBar
import com.yonasoft.jadedictionary.features.home.presentation.components.JadeBanner
import com.yonasoft.jadedictionary.features.home.presentation.components.LinkDirector

@Composable
fun Home(navController: NavHostController) {


    Scaffold(
        topBar = {
            HomeAppBar {
                navController.navigate(MainRoutes.Words.name)
            }
        },
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            JadeBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Slightly taller banner for better visual impact
            )

            Spacer(modifier = Modifier.height(24.dp)) // More space after banner

            // Updated section header with subtle accent
            Text(
                text = "EXPLORE",
                color = Color(0xFF64B5F6), // Light blue that matches the theme
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp, // Smaller size for minimalist look
                letterSpacing = 1.sp, // Letter spacing for modern look
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 24.dp, bottom = 8.dp)
            )

            // Link directors with improved spacing
            LinkDirector(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Filled.List,
                contentDescription = "Word Lists",
                label = "Word Lists",
                description = "Organize and manage your vocabulary",
                onClick = {
                    navController.navigate(MainRoutes.WordLists.name)
                },
            )

            LinkDirector(
                modifier = Modifier.fillMaxWidth(),
                icon = ImageVector.vectorResource(id = R.drawable.baseline_videogame_asset_24),
                contentDescription = "Practice",
                label = "Practice",
                description = "Test your knowledge with exercises and flash cards",
                onClick = {
                    navController.navigate(MainRoutes.Practice.name)
                },
            )

//            LinkDirector(
//                modifier = Modifier.fillMaxWidth(),
//                icon = Icons.Default.ShoppingCart,
//                contentDescription = "Store",
//                label = "Store",
//                description = "Get premium word lists, practice modules and learning tools",
//                onClick = {},
//                isComingSoon = true, // Mark the store as coming soon
//            )

            LinkDirector(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Settings,
                contentDescription = "Settings",
                label = "Settings",
                description = "Change and customize your app settings",
                onClick = {
                    navController.navigate(MainRoutes.Settings.name)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}