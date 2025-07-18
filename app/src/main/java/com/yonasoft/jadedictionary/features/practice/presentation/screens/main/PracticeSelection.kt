package com.yonasoft.jadedictionary.features.practice.presentation.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.PracticeRoutes
import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType
import com.yonasoft.jadedictionary.features.practice.domain.models.shared.WordSource
import com.yonasoft.jadedictionary.features.practice.presentation.components.PracticeOptionCard
import com.yonasoft.jadedictionary.features.practice.presentation.components.main.WordSourceSelector


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSelection(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    var wordSource: WordSource by rememberSaveable { mutableStateOf(WordSource.CUSTOM) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    // Define practice options
    val practiceOptions = listOf(
        Triple(
            PracticeType.FLASH_CARDS,
            ImageVector.vectorResource(R.drawable.outline_cards_star_24),
            "Review vocabulary with interactive flash cards"
        ),
        Triple(
            PracticeType.MULTIPLE_CHOICE,
            ImageVector.vectorResource(R.drawable.outline_view_list_24),
            "Test your knowledge with multiple choice questions"
        ),
        Triple(
            PracticeType.LISTENING,
            ImageVector.vectorResource(R.drawable.baseline_volume_up_24),
            "Improve your listening comprehension skills"
        )
    )

    Scaffold(
        containerColor = CustomColor.DARK01.color,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Choose Practice Type",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CustomColor.DARK02.color
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WordSourceSelector(selectedSource = wordSource, onSourceSelected = { wordSource = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Description text
            Text(
                text = "Select a practice type to get started",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Practice options
            practiceOptions.forEach { (practiceType, icon, description) ->
                PracticeOptionCard(
                    title = practiceType.displayName,
                    description = description,
                    icon = icon,
                    isLocked = false, // Example: Listening is locked
                    onClick = {
                        val route =
                            when (wordSource) {
                                WordSource.CUSTOM -> PracticeRoutes.CCPracticeSetup.createRoute(
                                    practiceType
                                )

                                WordSource.HSK -> PracticeRoutes.HSKPracticeSetup.createRoute(
                                    practiceType
                                )
                            }
                        navController.navigate(route)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}