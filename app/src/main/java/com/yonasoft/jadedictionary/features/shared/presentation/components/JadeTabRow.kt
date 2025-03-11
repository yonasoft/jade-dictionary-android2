package com.yonasoft.jadedictionary.features.shared.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun JadeTabRow(
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TabRow(
        modifier = modifier.padding(8.dp),
        selectedTabIndex = selectedIndex,
        containerColor = Color.Black,
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = Color.White
            )
        }
    ) {
        content()
    }
}