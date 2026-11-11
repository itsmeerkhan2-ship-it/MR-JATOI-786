package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.*

@Composable
fun VideoOverlayControls(
    video: Video,
    language: AppLanguage,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onReportClick: () -> Unit,
    onFollowClick: () -> Unit,
    onCreatorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotating Vinyl Disc Infinite Animation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp, start = 16.dp, end = 12.dp)
    ) {
        // Bottom Caption & Creator Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
        ) {
            // Creator Handle & Verified Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onCreatorClick() }
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = "@${video.creatorHandle}",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified Creator",
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Video Title / Caption
            Text(
                text = video.title,
                color = TextWhite,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Hashtags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                video.hashtags.take(3).forEach { tag ->
                    Text(
                        text = tag,
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Audio Ticker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(LuxuryBlack.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio",
                    tint = GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = video.musicTitle,
                    color = TextWhite,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Right Vertical Action Sidebar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            // Creator Avatar with Follow Badge
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                AsyncImage(
                    model = video.creatorAvatar,
                    contentDescription = video.creatorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, GoldPrimary, CircleShape)
                        .clickable { onCreatorClick() }
                )

                if (!video.isFollowing) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .offset(y = 8.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                            .clickable { onFollowClick() }
                            .testTag("follow_creator_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = AppStrings.get("follow", language),
                            tint = LuxuryBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Like Action
            ActionButton(
                icon = if (video.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                label = formatCount(video.likesCount),
                tint = if (video.isLiked) RedAlert else TextWhite,
                onClick = onLikeClick,
                testTag = "like_video_button"
            )

            // Comment Action
            ActionButton(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = formatCount(video.commentsCount),
                tint = TextWhite,
                onClick = onCommentClick,
                testTag = "comment_video_button"
            )

            // Share Action
            ActionButton(
                icon = Icons.Outlined.Share,
                label = formatCount(video.sharesCount),
                tint = TextWhite,
                onClick = onShareClick,
                testTag = "share_video_button"
            )

            // Report Action
            ActionButton(
                icon = Icons.Outlined.ReportProblem,
                label = AppStrings.get("report", language),
                tint = TextMuted,
                onClick = onReportClick,
                testTag = "report_video_button"
            )

            // Spinning Golden Vinyl Audio Disc
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GoldPrimary, LuxuryBlack, GoldDark)
                        )
                    )
                    .border(2.dp, LuxuryBorderBright, CircleShape)
                    .rotate(rotation)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(LuxuryBlack)
                        .border(1.dp, GoldLight, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(LuxuryBlack.copy(alpha = 0.45f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
