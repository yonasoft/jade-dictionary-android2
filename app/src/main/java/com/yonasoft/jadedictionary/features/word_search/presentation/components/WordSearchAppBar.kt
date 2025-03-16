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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.CreateWordListDialog


@Composable
fun WordSearchAppBar(
    navigateUp: () -> Unit,
    searchQuery: String,
    onCancel: () -> Unit,
    onValueChange: (String) -> Unit,
    createNewWordList: (title: String, description: String?) -> Unit,
    focusRequester: FocusRequester,
    snackbarHostState: SnackbarHostState
) {
    var helpExpanded by rememberSaveable { mutableStateOf(false) }
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showSnackbarTrigger by rememberSaveable { mutableStateOf<String?>(null) }

    // Move LaunchedEffect outside of the callback
    LaunchedEffect(showSnackbarTrigger) {
        showSnackbarTrigger?.let { title ->
            snackbarHostState.showSnackbar(
                message = "Word list \"$title\" created successfully",
                duration = SnackbarDuration.Short
            )
            // Reset after showing
            showSnackbarTrigger = null
        }
    }

    // Darker app bar color for better contrast
    val appBarColor = Color(0xFF050505)

    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = { navigateUp() },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        title = {
            SearchTextField(
                searchQuery = searchQuery,
                onValueChange = { onValueChange(it) },
                onCancel = { onCancel() },
                focusRequester = focusRequester,
                placeholder = "Search for words..."
            )
        },
        actions = {
            Box(
                modifier = Modifier.padding(end = 4.dp)
            ) {
                IconButton(
                    onClick = { helpExpanded = !helpExpanded },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_help_24),
                        contentDescription = "Help Icon",
                        tint = CustomColor.GREEN01.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
                HelpDropdown(
                    expanded = helpExpanded,
                    onDismiss = { helpExpanded = false },
                )
            }

            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_playlist_add_24),
                    contentDescription = "Add to word list",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appBarColor
        )
    )

    if (showCreateDialog) {
        CreateWordListDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, description ->
                createNewWordList(title, description)
                showCreateDialog = false
                showSnackbarTrigger = title
            }
        )
    }
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
                    .width(280.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent // Use transparent for gradient
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Remove elevation for cleaner look
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0D47A1), // Deeper blue start
                                    Color(0xFF1565C0)  // Lighter blue end
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
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 0.4.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                ) {
                                    append("Pinyin: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 14.sp
                                    )
                                ) {
                                    append("dian4 shi4, dian4shi4, diàn shì")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Light,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                ) {
                                    append("\nSpacing between syllables recommended")
                                }
                            },
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                ) {
                                    append("Definition: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 14.sp
                                    )
                                ) {
                                    append("Ice cream, Chinese language")
                                }
                            },
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                ) {
                                    append("Chinese: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 14.sp
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
