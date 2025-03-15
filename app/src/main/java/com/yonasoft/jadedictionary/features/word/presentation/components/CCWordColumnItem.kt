package com.yonasoft.jadedictionary.features.word.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.presentation.components.WordListSelectionDialog
import kotlinx.coroutines.launch

@Composable
fun CCWordColumn(
    word: CCWord,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    wordLists: List<CCWordList> = emptyList(),
    onAddToWordList: ((CCWord, CCWordList) -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
    actions: @Composable () -> Unit = {}
) {
    // Use remember to ensure we always display the full word, not just the first character
    val fullDisplayText = remember(word.displayText) {
        word.displayText // Use the entire string, not word.displayText.take(1)
    }

    val coroutineScope = rememberCoroutineScope()
    var showWordListDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp) // Reduced vertical padding
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            ) // Smaller elevation and corners
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121212)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Reduced elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Reduced padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(end = 8.dp)
                ) {
                    // Chinese character
                    Text(
                        text = fullDisplayText,
                        color = CustomColor.GREEN01.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, // Smaller font size
                        lineHeight = 24.sp,
                        overflow = TextOverflow.Visible
                    )

                    // Pinyin
                    Text(
                        text = word.displayPinyin,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp, // Smaller font size
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp,
                        overflow = TextOverflow.Visible
                    )
                }

                // Right side: Definition
                Text(
                    text = word.definition ?: "",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp, // Smaller font size
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.7f)
                )
            }

            if (onAddToWordList != null) {
                IconButton(
                    onClick = { showWordListDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to word list",
                        tint = CustomColor.GREEN01.color,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(0.9f)
                    )
                }
            }
            actions()
        }
    }

    // Word list selection dialog
    if (showWordListDialog) {
        WordListSelectionDialog(
            wordLists = wordLists,
            onDismiss = { showWordListDialog = false },
            onWordListSelected = { selectedList ->
                onAddToWordList?.let { it(word, selectedList) }

                // Show confirmation snackbar if available
                snackbarHostState?.let { hostState ->
                    coroutineScope.launch {
                        hostState.showSnackbar(
                            message = "Added \"${word.displayText}\" to ${selectedList.title}"
                        )
                    }
                }
            },

            )

    }
}