@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.features.word_search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.shared.presentation.components.SearchTextField

@Composable
fun WordSearchAppBar(
    navigateUp: () -> Unit,
    searchQuery: String,
    onCancel: () -> Unit,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    var helpExpanded by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = { navigateUp() },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
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
                    .padding(end = 8.dp)
            ) {
                IconButton(
                    onClick = { helpExpanded = !helpExpanded },
                    modifier = Modifier
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_help_24),
                        contentDescription = "Help Icon",
                        tint = CustomColor.GREEN01.color,
                        modifier = Modifier.size(24.dp)
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
            containerColor = Color(0xFF121212)
        ),
        modifier = Modifier.shadow(8.dp)
    )
}

@Composable
fun HelpDropdown(expanded: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    if (expanded) {
        val density = LocalDensity.current

        Popup(
            alignment = Alignment.TopEnd,
            onDismissRequest = onDismiss,
            offset = with(density) {
                androidx.compose.ui.unit.IntOffset(
                    x = (-16).dp.roundToPx(),
                    y = 60.dp.roundToPx()
                )
            }
        ) {
            Card(
                modifier = modifier
                    .width(280.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF202020)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1A237E).copy(alpha = 0.3f),
                                    Color(0xFF121212)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Search Tips",
                            color = CustomColor.GREEN01.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                ) {
                                    append("Pinyin: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 15.sp
                                    )
                                ) {
                                    append("dian4 shi4, dian4shi4, diàn shì")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Light,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                ) {
                                    append("\nSpacing between syllables recommended")
                                }
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                ) {
                                    append("Definition: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 15.sp
                                    )
                                ) {
                                    append("Ice cream, Chinese language")
                                }
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                ) {
                                    append("Chinese: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 15.sp
                                    )
                                ) {
                                    append("电视, 中文")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}