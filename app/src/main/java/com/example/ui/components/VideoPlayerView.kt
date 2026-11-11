package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.view.TextureView
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerView(
    video: Video,
    isActive: Boolean,
    onWatchEarned: () -> Unit,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var showPlayPauseIcon by remember { mutableStateOf(false) }
    var showHeartAnimation by remember { mutableStateOf(false) }
    var watchSeconds by remember { mutableIntStateOf(0) }
    var coinRewardEarned by remember { mutableStateOf(false) }

    // Anti-fraud watch timer: counts active watch seconds
    LaunchedEffect(isActive, isPlaying) {
        if (isActive && isPlaying) {
            while (true) {
                delay(1000L)
                watchSeconds++
                if (watchSeconds == 10 && !coinRewardEarned) {
                    coinRewardEarned = true
                    onWatchEarned()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryBlack)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isPlaying = !isPlaying
                        showPlayPauseIcon = true
                    },
                    onDoubleTap = {
                        showHeartAnimation = true
                        onDoubleTapLike()
                    }
                )
            }
    ) {
        // Native Android MediaPlayer + TextureView
        AndroidView(
            factory = { ctx ->
                val frameLayout = FrameLayout(ctx)
                val textureView = TextureView(ctx)
                frameLayout.addView(
                    textureView,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )

                var mediaPlayer: MediaPlayer? = null
                textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
                        try {
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(ctx, Uri.parse(video.videoUrl))
                                setSurface(android.view.Surface(surface))
                                isLooping = true
                                setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                                prepareAsync()
                                setOnPreparedListener { mp ->
                                    if (isActive && isPlaying) {
                                        mp.start()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}

                    override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean {
                        mediaPlayer?.release()
                        mediaPlayer = null
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
                }
                frameLayout.tag = mediaPlayer
                frameLayout
            },
            update = { view ->
                // Update play/pause if needed
            },
            modifier = Modifier.fillMaxSize()
        )

        // Poster / Thumbnail fallback background with subtle dim
        if (watchSeconds < 1) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Cinematic top and bottom shadow gradients for crisp overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            LuxuryBlack.copy(alpha = 0.75f),
                            Color.Transparent,
                            Color.Transparent,
                            LuxuryBlack.copy(alpha = 0.85f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Play / Pause flash feedback
        LaunchedEffect(showPlayPauseIcon) {
            if (showPlayPauseIcon) {
                delay(700)
                showPlayPauseIcon = false
            }
        }
        AnimatedVisibility(
            visible = showPlayPauseIcon,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(LuxuryBlack.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPlaying) "Playing" else "Paused",
                    tint = GoldPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Double-tap Big Heart Pop Animation
        LaunchedEffect(showHeartAnimation) {
            if (showHeartAnimation) {
                delay(900)
                showHeartAnimation = false
            }
        }
        AnimatedVisibility(
            visible = showHeartAnimation,
            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = RedAlert,
                modifier = Modifier.size(110.dp)
            )
        }

        // Top Left: Watch-to-Earn Live Coin Ring
        WatchToEarnRing(
            watchSeconds = watchSeconds,
            targetSeconds = 10,
            hasEarned = coinRewardEarned,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 56.dp)
        )
    }
}

@Composable
fun WatchToEarnRing(
    watchSeconds: Int,
    targetSeconds: Int,
    hasEarned: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = (watchSeconds.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = LuxuryBlack.copy(alpha = 0.75f),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorderBright),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(32.dp)
            ) {
                CircularProgressIndicator(
                    progress = { if (hasEarned) 1f else progress },
                    modifier = Modifier.fillMaxSize(),
                    color = GoldPrimary,
                    trackColor = LuxurySurfaceVariant,
                    strokeWidth = 3.dp
                )
                Icon(
                    imageVector = if (hasEarned) Icons.Default.CheckCircle else Icons.Default.MonetizationOn,
                    contentDescription = "Coin Reward",
                    tint = if (hasEarned) GreenSuccess else GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (hasEarned) "+5 Coins Earned!" else "Watch & Earn",
                    color = if (hasEarned) GoldPrimary else TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (hasEarned) "Verified session" else "${(targetSeconds - watchSeconds).coerceAtLeast(0)}s left",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
