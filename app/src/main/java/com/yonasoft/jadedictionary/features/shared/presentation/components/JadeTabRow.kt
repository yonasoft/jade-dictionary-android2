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
            .padding(horizontal = 16.dp, vertical = 4.dp) // Increased horizontal padding
            .height(48.dp),
        selectedTabIndex = selectedIndex,
        containerColor = Color(0xFF0A0A0A), // Slightly darker for better contrast with content
        contentColor = Color.White,
        divider = {
            if (dividerVisible) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF252525) // Slightly darker for better visibility
                )
            }
        },
        indicator = { tabPositions ->
            // Only show the indicator if we have valid tab positions
            if (tabs > 0 && selectedIndex < tabs && tabPositions.isNotEmpty()) {
                SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .padding(horizontal = 12.dp), // Add some padding for shorter indicator
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
            .padding(horizontal = 16.dp, vertical = 4.dp) // Increased horizontal padding
            .height(48.dp),
        selectedTabIndex = selectedIndex,
        containerColor = Color(0xFF0A0A0A), // Slightly darker for better contrast with content
        contentColor = Color.White,
        divider = {
            if (dividerVisible) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF252525) // Slightly darker for better visibility
                )
            }
        },
        indicator = { tabPositions ->
            if (tabs > 0 && selectedIndex < tabs && tabPositions.isNotEmpty()) {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .padding(horizontal = 20.dp), // Increased padding for shorter indicator
                    height = 3.dp, // Slightly shorter for cleaner look
                    color = CustomColor.GREEN01.color
                )
            }
        }
    ) {
        content()
    }
}