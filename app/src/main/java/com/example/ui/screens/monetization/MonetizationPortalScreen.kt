package com.example.ui.screens.monetization

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationStatus
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.*

@Composable
fun MonetizationPortalScreen(
    repository: AppRepository,
    language: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by repository.currentUser.collectAsState()
    val applications by repository.monetizationApplications.collectAsState()

    val myApplication = applications.find { it.userId == currentUser.id }

    // Targets
    val targetFollowers = 500_000L
    val targetViews = 100_000L
    val targetHours = 4_000L

    val followersProgress = (currentUser.followersCount.toFloat() / targetFollowers.toFloat()).coerceIn(0f, 1f)
    val viewsProgress = (currentUser.verifiedViews.toFloat() / targetViews.toFloat()).coerceIn(0f, 1f)
    val hoursProgress = (currentUser.verifiedWatchHours.toFloat() / targetHours.toFloat()).coerceIn(0f, 1f)

    val isAllEligible = currentUser.followersCount >= targetFollowers &&
            currentUser.verifiedViews >= targetViews &&
            currentUser.verifiedWatchHours >= targetHours &&
            currentUser.communityStrikes == 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = GoldPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = AppStrings.get("creator_monetization", language),
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Official Partner Program & Guidelines",
                    color = GoldLight,
                    fontSize = 11.sp
                )
            }
        }

        // Hero Golden Badge Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LuxuryBlack),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, LuxuryBorderBright),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(GoldContainer, LuxuryBlack)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = LuxuryBlack,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "MR JATOI CREATOR EARNINGS",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Turn your creativity into reliable income",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "All earnings, views, and watch times undergo strict server verification. Admin approval is compulsory.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Status Card
        if (myApplication != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (myApplication.status) {
                        ApplicationStatus.APPROVED -> GreenSuccess.copy(alpha = 0.15f)
                        ApplicationStatus.PENDING -> OrangePending.copy(alpha = 0.15f)
                        ApplicationStatus.REJECTED -> RedAlert.copy(alpha = 0.15f)
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (myApplication.status) {
                        ApplicationStatus.APPROVED -> GreenSuccess
                        ApplicationStatus.PENDING -> OrangePending
                        ApplicationStatus.REJECTED -> RedAlert
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (myApplication.status) {
                            ApplicationStatus.APPROVED -> Icons.Default.CheckCircle
                            ApplicationStatus.PENDING -> Icons.Default.HourglassTop
                            ApplicationStatus.REJECTED -> Icons.Default.Cancel
                        },
                        contentDescription = null,
                        tint = when (myApplication.status) {
                            ApplicationStatus.APPROVED -> GreenSuccess
                            ApplicationStatus.PENDING -> OrangePending
                            ApplicationStatus.REJECTED -> RedAlert
                        },
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = myApplication.status.title,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = myApplication.adminReviewNotes,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5 Compulsory Requirements Header
        Text(
            text = "Eligibility Requirements",
            color = GoldLight,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Req 1: 500,000 Followers
        RequirementCard(
            icon = Icons.Default.People,
            title = AppStrings.get("monetization_req_1", language),
            currentValue = "${currentUser.followersCount}",
            targetValue = "500,000",
            progress = followersProgress,
            isCompleted = currentUser.followersCount >= targetFollowers
        )

        // Req 2: 100,000 Verified Views
        RequirementCard(
            icon = Icons.Default.Visibility,
            title = AppStrings.get("monetization_req_2", language),
            currentValue = "${currentUser.verifiedViews}",
            targetValue = "100,000",
            progress = viewsProgress,
            isCompleted = currentUser.verifiedViews >= targetViews
        )

        // Req 3: 4,000 Watch Hours
        RequirementCard(
            icon = Icons.Default.AccessTime,
            title = AppStrings.get("monetization_req_3", language),
            currentValue = "${currentUser.verifiedWatchHours} hrs",
            targetValue = "4,000 hrs",
            progress = hoursProgress,
            isCompleted = currentUser.verifiedWatchHours >= targetHours
        )

        // Req 4: Community Guidelines
        RequirementCard(
            icon = Icons.Default.Gavel,
            title = AppStrings.get("monetization_req_4", language),
            currentValue = "${currentUser.communityStrikes} strikes",
            targetValue = "0 strikes",
            progress = if (currentUser.communityStrikes == 0) 1f else 0f,
            isCompleted = currentUser.communityStrikes == 0
        )

        // Req 5: Admin Approval Compulsory
        RequirementCard(
            icon = Icons.Default.VerifiedUser,
            title = AppStrings.get("monetization_req_5", language),
            currentValue = if (currentUser.isMonetized) "Approved" else "Pending Review",
            targetValue = "Admin Signoff",
            progress = if (currentUser.isMonetized) 1f else 0.5f,
            isCompleted = currentUser.isMonetized
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Button
        Button(
            onClick = {
                val (success, message) = repository.applyForMonetization()
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            },
            enabled = myApplication == null || myApplication.status == ApplicationStatus.REJECTED,
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldPrimary,
                contentColor = LuxuryBlack,
                disabledContainerColor = LuxurySurfaceVariant,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp)
                .testTag("apply_monetization_button")
        ) {
            Text(
                text = if (myApplication != null) "Application Submitted" else AppStrings.get("apply_monetization", language),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RequirementCard(
    icon: ImageVector,
    title: String,
    currentValue: String,
    targetValue: String,
    progress: Float,
    isCompleted: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isCompleted) GreenSuccess else GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isCompleted) GreenSuccess else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isCompleted) GreenSuccess else GoldPrimary,
                trackColor = LuxuryDarkBg
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current: $currentValue",
                    color = GoldLight,
                    fontSize = 11.sp
                )
                Text(
                    text = "Required: $targetValue",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
