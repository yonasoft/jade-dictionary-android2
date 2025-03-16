// Improved SearchTextField
package com.yonasoft.jadedictionary.features.shared.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.core.constants.CustomColor

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onValueChange: (String) -> Unit,
    onCancel: () -> Unit = {},
    placeholder: String = "Search...",
    focusRequester: FocusRequester? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        TextField(
            value = searchQuery,
            singleLine = true,
            onValueChange = {
                onValueChange(it)
            },
            placeholder = {
                Text(
                    placeholder,
                    color = Color.White.copy(alpha = 0.5f), // Slightly more transparent for subtlety
                    fontSize = 16.sp, // Slightly smaller for cleaner look
                    letterSpacing = 0.3.sp // Slightly increased letter spacing
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = CustomColor.GREEN01.color,
                    modifier = Modifier.size(22.dp) // Slightly smaller icon
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(32.dp) // Slightly smaller for cleaner look
                            .clip(CircleShape)
                            .background(Color(0xFF252525)) // Slightly darker for better contrast
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = Color.White.copy(alpha = 0.8f), // Slightly transparent for subtlety
                            modifier = Modifier.size(18.dp) // Slightly smaller icon
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF121212), // Darker background for better contrast
                unfocusedContainerColor = Color(0xFF121212),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = CustomColor.GREEN01.color,
            ),
            textStyle = TextStyle.Default.copy(
                fontSize = 16.sp, // Slightly smaller for cleaner look
                letterSpacing = 0.3.sp // Slightly increased letter spacing
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                hintLocales = LocaleList(
                    Locale("en"),
                    Locale("zh"),
                ),
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Slightly shorter for cleaner look
                .clip(RoundedCornerShape(28.dp)) // Half of height for perfect pill shape
                .let { base ->
                    focusRequester?.let { base.focusRequester(it) } ?: base
                },
            maxLines = 2
        )
    }
}