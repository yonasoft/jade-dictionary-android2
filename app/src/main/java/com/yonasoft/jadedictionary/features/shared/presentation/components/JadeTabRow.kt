package com.yonasoft.jadedictionary.features.shared.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.core.constants.CustomColor

@Composable
fun JadeTabRow(
    selectedIndex: Int,
    tabs: Int = 0, // Number of tabs, used for indicator animation
    dividerVisible: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TabRow(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(48.dp),
        selectedTabIndex = selectedIndex,
        containerColor = Color(0xFF121212), // Slightly lighter than pure black for better depth
        contentColor = Color.White,
        divider = {
            if (dividerVisible) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF333333) // Subtle divider color
                )
            }
        },
        indicator = { tabPositions ->
            // Only show the indicator if we have valid tab positions
            if (tabs > 0 && selectedIndex < tabs && tabPositions.isNotEmpty()) {
                SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .padding(horizontal = 8.dp), // Add some padding to make indicator shorter than tab
                    height = 3.dp, // Thicker indicator for better visibility
                    color = CustomColor.GREEN01.color
                )
            }
        }
    ) {
        content()
    }
}

// Alternative version with rectangular indicator but squared corners
@Composable
fun JadeTabRowAlternative(
    selectedIndex: Int,
    tabs: Int = 0,
    dividerVisible: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TabRow(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(48.dp),
        selectedTabIndex = selectedIndex,
        containerColor = Color(0xFF121212),
        contentColor = Color.White,
        divider = {
            if (dividerVisible) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF333333)
                )
            }
        },
        indicator = { tabPositions ->
            if (tabs > 0 && selectedIndex < tabs && tabPositions.isNotEmpty()) {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .padding(horizontal = 16.dp),
                    height = 4.dp,
                    color = CustomColor.GREEN01.color
                )
            }
        }
    ) {
        content()
    }
}