package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTutorialGuideSheet(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lessons = listOf(
        Pair("1. Create Apps on Mobile Phone", "Step-by-step navigation in mobile browser to launch Google AI Studio and interact with Gemini build tools without a PC."),
        Pair("2. What is Google AI Studio?", "Introduction to the developer platform, workspace setup, project templates, and Gemini 2.5/Flash model capabilities."),
        Pair("3. No-Code App Architecture", "How natural language prompt engineering generates complete Kotlin, Jetpack Compose, and Android manifests automatically."),
        Pair("4. Writing High-Impact Prompts", "Formula for specifying UI theme, navigation routes, data persistence, and accessibility requirements cleanly."),
        Pair("5. Adding New Features Iteratively", "How to converse with AI to add monetization portals, payment gateways, and multilingual localization without breaking existing code."),
        Pair("6. Customizing Design & Branding", "Custom palette integration (luxury black & gold), adaptive vector app icons, and Material 3 components."),
        Pair("7. Testing & Exporting Your App", "Running live emulator tests, verifying unit tests, exporting APK/AAB bundles, and GitHub synchronization.")
    )

    val samplePrompts = listOf(
        "Create an Android video-sharing and earning application named MR JATOI VIDEO with black & golden theme, TikTok-style feed, JazzCash & Easypaisa payout.",
        "Create a Pakistani Halal Food & Recipe guide with step-by-step videos, Urdu/English language toggle, and bookmarking.",
        "Create a daily habit and prayer tracker with notification reminders, streak coins, and offline Room database."
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LuxurySurface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LuxuryBorderBright)
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = LuxuryBlack, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MR JATOI CREATOR ACADEMY",
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Build Apps with Google AI Studio",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
            }

            // Description Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryBlack),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorderBright),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(GoldContainer.copy(alpha = 0.5f), LuxuryBlack)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Turn App Ideas into Reality Without Learning Programming!",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Whether you are a student, beginner, YouTuber, entrepreneur, or digital creator in Pakistan or worldwide, this tutorial teaches you how to build complete applications using simple text prompts.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // What You Will Learn Section
            item {
                Text(
                    text = "What You Will Learn in This Video",
                    color = GoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(lessons) { (title, desc) ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryDarkBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = title,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = desc,
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Sample Prompts to Try
            item {
                Text(
                    text = "Sample Prompts to Build Your Apps (Tap to Copy)",
                    color = GoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(samplePrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LuxuryDarkBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("AI Studio Prompt", prompt)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Prompt copied! Paste it in Google AI Studio.", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = prompt,
                            color = TextWhite,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Prompt",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Pricing & Usage Notes
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryBlack),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Note: Google AI Studio offers free usage with certain rate limits. Some advanced features, custom models, or additional services may require a billing account or payment.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
