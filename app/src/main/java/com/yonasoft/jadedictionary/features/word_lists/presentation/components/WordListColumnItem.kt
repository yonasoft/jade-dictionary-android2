package com.yonasoft.jadedictionary.features.word_lists.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WordListColumnItem(
    wordList: WordList,
    onClick: () -> Unit,
    onDelete: ((CCWordList) -> Unit)? = null,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            wordList = wordList,
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                onDelete?.let { it1 -> it1(wordList as CCWordList) }
                showDeleteDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121212)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Title
                Text(
                    text = wordList.title,
                    color = CustomColor.GREEN01.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                // Word count
                Text(
                    text = "${wordList.numberOfWords} word${if (wordList.numberOfWords != 1.toLong()) "s" else ""}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                )

                // Description (if available)
                if (wordList.description.isNotEmpty()) {
                    Text(
                        text = wordList.description,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Date info
                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                val updatedDate = dateFormat.format(Date(wordList.updatedAt))

                Text(
                    text = "Updated: $updatedDate",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                )
            }
            if (onDelete != null) {
                // Delete button
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete word list",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(20.dp)
                            .alpha(0.8f)
                    )
                }
            }
        }
    }
}