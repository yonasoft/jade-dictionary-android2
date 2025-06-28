@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.features.home.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.R

@Composable
fun HomeAppBar(onClickSearch: () -> Unit) {
    val appBarColor = Color(0xFF050505)
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    TopAppBar(
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.jade_icon),
                contentDescription = "Jade Icon",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
            )
        },
        title = {
            HomeSearchBar {
                onClickSearch()
            }
        },
        actions = {
            // Dropdown menu button
            IconButton(
                onClick = { showDropdown = true }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.White
                )
            }

            // Dropdown menu
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier.background(Color(0xFF1A1A1A))
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Contact",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Contact Me",
                                color = Color.White
                            )
                        }
                    },
                    onClick = {
                        showDropdown = false
                        showContactDialog = true
                    }
                )

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Rate Me",
                                color = Color.White
                            )
                        }
                    },
                    onClick = {
                        showDropdown = false
                        showRateDialog = true
                    }
                )

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Share App",
                                color = Color.White
                            )
                        }
                    },
                    onClick = {
                        showDropdown = false
                        showShareDialog = true
                    }
                )

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "About",
                                color = Color.White
                            )
                        }
                    },
                    onClick = {
                        showDropdown = false
                        showAboutDialog = true
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appBarColor
        )
    )

    // Contact Dialog
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Get in Touch!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "I'd love to hear from you! 😊",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Whether you have suggestions, found a bug, or just want to say hi - your feedback helps make Jade Dictionary better for everyone!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Feel free to share whatever's on your mind. 💭",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showContactDialog = false
                        sendFeedbackEmail(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF64B5F6)
                    )
                ) {
                    Text("Send Email", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showContactDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Maybe Later")
                }
            }
        )
    }

    // Rate Dialog
    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rate Jade Dictionary",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Enjoying the app? ⭐",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your rating really helps other learners discover Jade Dictionary! Even a quick review makes a huge difference.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "But no pressure - rate only if you want to! 😊",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRateDialog = false
                        openPlayStore(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    )
                ) {
                    Text("Rate App", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRateDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Not Now")
                }
            }
        )
    }

    // Share Dialog
    if (showShareDialog) {
        ShareAppDialog(
            onDismiss = { showShareDialog = false },
            context = context
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AboutAppDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}

@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    onClickSearch: () -> Unit
) {
    val searchBarColor = Color(0xFF1A1A1A)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(23.dp))
            .background(color = searchBarColor)
            .clickable { onClickSearch() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(20.dp),
            tint = Color.White.copy(alpha = 0.6f)
        )

        Text(
            text = "Search for words...",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
        )
    }
}

// Helper functions for menu actions
private fun sendFeedbackEmail(context: Context) {
    val subject = "Jade Dictionary - Feedback & Suggestions"
    val body = """
        Hi there!
        
        I'd love to hear your thoughts about Jade Dictionary! Whether it's feedback, suggestions for new features, bug reports, or just general comments - everything helps make the app better.
        
        What's on your mind?
        
        ---
        Thanks for using Jade Dictionary!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("yonasoft7@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // If no email app, intent won't launch
    }
}

private fun openPlayStore(context: Context) {
    try {
        // Try to open in Play Store app first
        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.yonasoft.jadedictionary"))
        context.startActivity(playStoreIntent)
    } catch (e: Exception) {
        // Fallback to web browser
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.yonasoft.jadedictionary&hl=en_US"))
        context.startActivity(webIntent)
    }
}

@Composable
fun ShareAppDialog(
    onDismiss: () -> Unit,
    context: Context
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Jade Dictionary",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Help others discover Chinese learning! 🚀",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Share Jade Dictionary with friends, family, or anyone interested in learning Chinese characters and HSK vocabulary.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    shareApp(context)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF64B5F6)
                )
            ) {
                Text("Share App", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AboutAppDialog(onDismiss: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("App Info", "Features", "Updates")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Column {
                Text(
                    text = "Jade Dictionary",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        )
                    }
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> AppInfoTab()
                    1 -> FeaturesTab()
                    2 -> UpdatesTab()
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF64B5F6)
                )
            ) {
                Text("Close", color = Color.White)
            }
        },
        dismissButton = null
    )
}

@Composable
fun AppInfoTab() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        InfoCard(
            title = "Version",
            content = "2.1"
        )
        InfoCard(
            title = "Updated",
            content = "Jun 28, 2025"
        )
        InfoCard(
            title = "Requirements",
            content = "Android 8.0 and up"
        )
        InfoCard(
            title = "Download Size",
            content = "~50 MB"
        )
        InfoCard(
            title = "Downloads",
            content = "40+ downloads"
        )
        InfoCard(
            title = "Developer",
            content = "Yonasoft"
        )
        InfoCard(
            title = "Released",
            content = "Mar 28, 2024"
        )
        InfoCard(
            title = "Content Rating",
            content = "Everyone 10+"
        )
    }
}

@Composable
fun FeaturesTab() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FeatureCard(
            title = "📚 Comprehensive Dictionary",
            description = "120,000+ Chinese-English entries with pronunciation guides and example sentences"
        )
        FeatureCard(
            title = "✍️ Multiple Input Methods",
            description = "Handwriting recognition, OCR camera input, speech-to-text, and keyboard entry"
        )
        FeatureCard(
            title = "🎯 Complete HSK Coverage",
            description = "HSK 2.0 (levels 1-6) and HSK 3.0 (levels 1-9) vocabulary sets for exam prep"
        )
        FeatureCard(
            title = "🎓 Effective Practice",
            description = "Flashcards, multiple-choice quizzes, and listening exercises"
        )
        FeatureCard(
            title = "📝 Personalized Learning",
            description = "Create custom word lists and focus on vocabulary relevant to your needs"
        )
    }
}

@Composable
fun UpdatesTab() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Coming Soon! 🚀",
            color = Color(0xFF64B5F6),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        UpcomingFeature("📊 Word stats from practice sessions")
        UpcomingFeature("🏆 Achievements & progress tracking")
        UpcomingFeature("🧠 SRS (Spaced Repetition System)")
        UpcomingFeature("🎮 New practice modes & games")
        UpcomingFeature("💾 Sync data to Google Play account")
        UpcomingFeature("📱 Better tablet support")
        UpcomingFeature("🛍️ Shop for premium content")
        UpcomingFeature("📋 Preset vocabulary lists")

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Recent Updates",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "• Practice Reminders\n• Customizable app settings\n• Update Notifications",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 13.sp
        )
    }
}

@Composable
fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = content,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FeatureCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun UpcomingFeature(feature: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            color = Color(0xFF64B5F6),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = feature,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 13.sp
        )
    }
}

private fun shareApp(context: Context) {
    val shareText = """
        🚀 Check out Jade Dictionary - the ultimate Chinese learning app!
        
        📚 120,000+ Chinese-English entries
        ✍️ Handwriting & OCR recognition  
        🎯 Complete HSK 2.0 & 3.0 coverage
        🎓 Flashcards & practice modes
        
        Perfect for learning Chinese characters and HSK vocabulary!
        
        Download: https://play.google.com/store/apps/details?id=com.yonasoft.jadedictionary&hl=en_US
    """.trimIndent()

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, "Jade Dictionary - Chinese Learning App")
    }

    try {
        context.startActivity(Intent.createChooser(shareIntent, "Share Jade Dictionary"))
    } catch (e: Exception) {
        // Handle error if no sharing apps available
    }
}