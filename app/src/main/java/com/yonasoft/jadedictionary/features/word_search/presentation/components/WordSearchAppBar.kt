@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.features.word_search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.features.shared.presentation.components.SearchTextField

@Composable
fun WordSearchAppBar(
    navigateUp: () -> Unit,
    searchQuery: String,
    onCancel: () -> Unit,
    onValueChange: (String) -> Unit, focusRequester: FocusRequester
) {
    var helpExpanded by rememberSaveable { mutableStateOf(false) }

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
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { helpExpanded = !helpExpanded }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_help_24),
                        contentDescription = "Help Icon",
                        tint = Color.LightGray,
                        modifier = Modifier
                            .size(32.dp)

                    )
                }
                HelpDropdown(
                    expanded = helpExpanded,
                    onDismiss = {
                        helpExpanded = false
                    },
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        ),
    )
}

@Composable
fun HelpDropdown(expanded: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    DropdownMenu(
        modifier =
        modifier
            .background(color = Color.Black)
            .width(250.dp)
            .padding(8.dp),
        expanded = expanded,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Search With: \n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nPinyin: ")
                    }
                    append("e.g. dian4 shi4, dian4shi4, diàn shì")
                    append("\n*Adding spaces between pinyin syllables is recommended")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\n\nDefinition: ")
                    }
                    append("e.g. Ice cream, Chinese language")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\n\nChinese: ")
                    }
                    append("e.g. 电视, 中文")
                },
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}
