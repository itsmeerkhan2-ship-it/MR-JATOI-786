package com.example.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Comment
import com.example.data.model.Video
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ReportVideoDialog
import com.example.ui.components.ShareVideoDialog
import com.example.ui.components.VideoOverlayControls
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: AppRepository,
    language: AppLanguage,
    onNavigateToWallet: () -> Unit,
    onNavigateToCreator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videos by repository.videos.collectAsState()
    val commentsMap by repository.comments.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var selectedTab by remember { mutableIntStateOf(1) } // 0: Following, 1: For You
    val displayedVideos = remember(selectedTab, videos) {
        if (selectedTab == 0) videos.filter { it.isFollowing } else videos
    }

    val pagerState = rememberPagerState(pageCount = { displayedVideos.size })

    // Active modals
    var activeCommentVideo by remember { mutableStateOf<Video?>(null) }
    var activeShareVideo by remember { mutableStateOf<Video?>(null) }
    var activeReportVideo by remember { mutableStateOf<Video?>(null) }

    Box(modifier = modifier.fillMaxSize().background(LuxuryBlack)) {
        if (displayedVideos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (selectedTab == 0) "No followed creators yet" else "No videos available",
                        color = TextWhite,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Follow creators in 'For You' to see their videos here!",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val video = displayedVideos[page]
                val isActive = page == pagerState.currentPage

                Box(modifier = Modifier.fillMaxSize()) {
                    VideoPlayerView(
                        video = video,
                        isActive = isActive,
                        onWatchEarned = {
                            val earned = repository.recordVideoView(video.id, 12)
                            if (earned) {
                                Toast.makeText(
                                    context,
                                    "+5 Coins added to your Wallet for genuine watch time!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        onDoubleTapLike = {
                            if (!video.isLiked) {
                                repository.toggleLikeVideo(video.id)
                            }
                        }
                    )

                    VideoOverlayControls(
                        video = video,
                        language = language,
                        onLikeClick = { repository.toggleLikeVideo(video.id) },
                        onCommentClick = { activeCommentVideo = video },
                        onShareClick = { activeShareVideo = video },
                        onReportClick = { activeReportVideo = video },
                        onFollowClick = { repository.toggleFollowCreator(video.creatorId) },
                        onCreatorClick = { onNavigateToCreator(video.creatorId) }
                    )
                }
            }
        }

        // Top Navigation Tabs & Coins Widget
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Spacer to balance coins pill
            Box(modifier = Modifier.width(90.dp))

            // Center: Following | For You
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get("following", language),
                    color = if (selectedTab == 0) TextWhite else TextMuted,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedTab = 0 }
                )
                Text(
                    text = "|",
                    color = LuxuryBorderBright,
                    fontSize = 14.sp
                )
                Text(
                    text = AppStrings.get("for_you", language),
                    color = if (selectedTab == 1) GoldPrimary else TextMuted,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedTab = 1 }
                )
            }

            // Right: Earning Wallet pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = LuxuryBlack.copy(alpha = 0.65f),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorderBright),
                modifier = Modifier.clickable { onNavigateToWallet() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${currentUser.coins}",
                        color = GoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Comments Bottom Sheet
        activeCommentVideo?.let { v ->
            val comments = commentsMap[v.id] ?: emptyList()
            CommentsBottomSheet(
                comments = comments,
                language = language,
                onDismiss = { activeCommentVideo = null },
                onAddComment = { content ->
                    repository.addComment(v.id, content)
                },
                onLikeComment = { commentId ->
                    repository.toggleCommentLike(v.id, commentId)
                }
            )
        }

        // Share Dialog
        activeShareVideo?.let { v ->
            ShareVideoDialog(
                video = v,
                language = language,
                onDismiss = { activeShareVideo = null }
            )
        }

        // Report Dialog
        activeReportVideo?.let { v ->
            ReportVideoDialog(
                videoId = v.id,
                language = language,
                onDismiss = { activeReportVideo = null },
                onSubmitReport = { reason ->
                    repository.reportVideo(v.id, reason)
                    Toast.makeText(
                        context,
                        AppStrings.get("report_submitted", language),
                        Toast.LENGTH_LONG
                    ).show()
                    activeReportVideo = null
                }
            )
        }
    }
}
