package com.yonasoft.jadedictionary.features.word_search.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.features.word_search.presentation.components.WordDetailAppbar
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WordDetail(
    navController: NavHostController,
    wordId: Long? = null,
    wordDetailViewModel: WordDetailViewModel = koinViewModel(),
) {
    Scaffold(
        topBar = {
            WordDetailAppbar(navigateUp = {
                navController.navigateUp()
            })
        },
        containerColor = Color.Black
    ) { paddingValue ->
        Column(modifier = Modifier.padding(paddingValue)) {

        }
    }
}