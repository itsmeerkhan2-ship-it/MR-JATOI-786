package com.example.ui.screens.upload

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UploadScreen(
    repository: AppRepository,
    language: AppLanguage,
    onUploadSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Entertainment") }
    var hashtagsInput by remember { mutableStateOf("#mrjatoivideo #pakistan #viral") }
    var selectedVideoUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4") }
    var thumbnailUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80") }

    val categories = listOf("Entertainment", "Technology", "Pakistani Culture", "Food & Travel", "Music", "Earning Guide", "Sports")

    val sampleClips = listOf(
        Pair("Blazes (Cinematic)", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"),
        Pair("Escapes (Nature)", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
        Pair("Joy (Lifestyle)", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4"),
        Pair("Fun (Comedy)", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
    )

    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<String?>(null) }
    var scanPassed by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = AppStrings.get("upload_video", language),
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "MR JATOI VIDEO Creator Studio",
                    color = GoldLight,
                    fontSize = 12.sp
                )
            }
        }

        // Title Input
        Text(
            text = AppStrings.get("video_title", language),
            color = GoldLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                scanResult = null
            },
            placeholder = { Text("Enter an engaging title or caption...", color = TextMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = LuxuryBorder,
                focusedContainerColor = LuxurySurface,
                unfocusedContainerColor = LuxurySurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("upload_title_input")
        )

        // Category Picker
        Text(
            text = AppStrings.get("select_category", language),
            color = GoldLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(categories) { cat ->
                val isSelected = category == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { category = cat },
                    label = { Text(cat, color = if (isSelected) LuxuryBlack else TextWhite) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        containerColor = LuxurySurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) GoldPrimary else LuxuryBorder
                    )
                )
            }
        }

        // Hashtags Input
        Text(
            text = AppStrings.get("add_hashtags", language),
            color = GoldLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = hashtagsInput,
            onValueChange = { hashtagsInput = it },
            placeholder = { Text("#trending #pakistan #mrjatoi", color = TextMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = LuxuryBorder,
                focusedContainerColor = LuxurySurface,
                unfocusedContainerColor = LuxurySurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("upload_hashtags_input")
        )

        // Video Source Selector
        Text(
            text = "Video Source (Cloud Streaming Clip)",
            color = GoldLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            sampleClips.forEach { (name, url) ->
                val isSelected = selectedVideoUrl == url
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) LuxurySurfaceVariant else LuxurySurface)
                        .border(1.dp, if (isSelected) GoldPrimary else LuxuryBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedVideoUrl = url }
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) GoldPrimary else TextMuted
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = name,
                        color = if (isSelected) GoldLight else TextWhite,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // AI Safety & Moderation Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = LuxurySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorderBright),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("ai_moderation_title", language),
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "MR JATOI AI checks for community guideline compliance, prevents bot views, harmful spam, and copyright strikes.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                if (isScanning) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = GoldPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("running_ai_scan", language),
                            color = GoldLight,
                            fontSize = 12.sp
                        )
                    }
                } else if (scanResult != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (scanPassed) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (scanPassed) GreenSuccess else RedAlert,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = scanResult ?: "",
                            color = if (scanPassed) GreenSuccess else RedAlert,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Publish Button
        Button(
            onClick = {
                if (title.isBlank()) {
                    Toast.makeText(context, "Please enter a video title", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                coroutineScope.launch {
                    isScanning = true
                    delay(1200L) // AI Deep Scan simulation
                    isScanning = false

                    val tags = hashtagsInput.split(" ")
                        .filter { it.isNotBlank() }
                        .map { if (it.startsWith("#")) it else "#$it" }

                    val (success, message) = repository.uploadVideo(
                        title = title.trim(),
                        category = category,
                        hashtags = tags,
                        videoUrl = selectedVideoUrl,
                        thumbnailUrl = thumbnailUrl
                    )

                    scanResult = message
                    scanPassed = success

                    if (success) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        delay(600L)
                        onUploadSuccess()
                    }
                }
            },
            enabled = !isScanning,
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldPrimary,
                contentColor = LuxuryBlack
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("publish_video_button")
        ) {
            Text(
                text = AppStrings.get("publish_now", language),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
