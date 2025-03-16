// Improved CreateWordListDialog
package com.yonasoft.jadedictionary.features.word_lists.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yonasoft.jadedictionary.core.constants.CustomColor

@Composable
fun CreateWordListDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String?) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var titleError by rememberSaveable { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp), // Larger corners for more modern look
            color = Color(0xFF0A0A0A) // Slightly darker for better contrast
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create New Word List",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp // Slight letter spacing for modern look
                )

                Spacer(modifier = Modifier.height(20.dp)) // Increased space

                // Title TextField
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = it.isBlank()
                    },
                    label = {
                        Text(
                            "Title (Required)",
                            letterSpacing = 0.3.sp
                        )
                    },
                    isError = titleError,
                    supportingText = {
                        if (titleError) {
                            Text("Title cannot be empty", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CustomColor.GREEN01.color,
                        focusedLabelColor = CustomColor.GREEN01.color,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f), // More subtle when unfocused
                        unfocusedLabelColor = Color.Gray.copy(alpha = 0.7f),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description TextField
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            "Description (Optional)",
                            letterSpacing = 0.3.sp
                        )
                    },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CustomColor.GREEN01.color,
                        focusedLabelColor = CustomColor.GREEN01.color,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f), // More subtle when unfocused
                        unfocusedLabelColor = Color.Gray.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(
                            "Cancel",
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, description.ifBlank { "" })
                            } else {
                                titleError = true
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomColor.GREEN01.color,
                            contentColor = Color.Black,
                            disabledContainerColor = CustomColor.GREEN01.color.copy(alpha = 0.5f),
                            disabledContentColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp) // Slightly rounded corners for buttons
                    ) {
                        Text(
                            "Create",
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}