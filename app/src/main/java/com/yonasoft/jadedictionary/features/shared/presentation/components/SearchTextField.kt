package com.yonasoft.jadedictionary.features.shared.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchTextField(modifier: Modifier = Modifier) {
    TextField(
        value = "",
        onValueChange = {

        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Black,
            unfocusedContainerColor = Color.Black,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.DarkGray,
            unfocusedIndicatorColor = Color.DarkGray,
            unfocusedLabelColor = Color.DarkGray,
            cursorColor = lerp(Color.Green, Color.White, .5f)
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Back arrow",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {

            }
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}