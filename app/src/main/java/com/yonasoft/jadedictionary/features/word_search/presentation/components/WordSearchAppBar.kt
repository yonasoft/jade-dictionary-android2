@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.features.word_search.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.features.shared.presentation.components.SearchTextField

@Composable
fun WordSearchAppBar(
    navigateUp: () -> Unit,
    searchQuery: String,
    onCancel: () -> Unit,
    onValueChange: (String) -> Unit, focusRequester: FocusRequester
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = { navigateUp() },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            SearchTextField(
                searchQuery = searchQuery,
                onValueChange = { onValueChange(it) },
                onCancel = {
                    onCancel()
                },
                focusRequester = focusRequester
            )
        },
        actions = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_help_24),
                contentDescription = "Help Icon",
                tint = Color.LightGray,
                modifier = Modifier.padding(horizontal = 4.dp).size(32.dp),
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        ),
    )
}

