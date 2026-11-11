package com.example.ui.screens.admin

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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationStatus
import com.example.data.model.MonetizationApplication
import com.example.data.model.ReportTicket
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.WalletTransaction
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    repository: AppRepository,
    language: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val applications by repository.monetizationApplications.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val reports by repository.reports.collectAsState()
    val videos by repository.videos.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Monetization, 1: Payments, 2: Reports, 3: Users

    val pendingWithdrawals = remember(transactions) {
        transactions.filter { it.type == TransactionType.WITHDRAWAL }
    }
    val pendingApplications = remember(applications) {
        applications.filter { it.status == ApplicationStatus.PENDING }
    }
    val activeReports = remember(reports) {
        reports.filter { !it.isResolved }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .statusBarsPadding()
    ) {
        // Top Admin Header with Gold Trim
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxuryBlack)
                .border(androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MR JATOI ADMIN PORTAL",
                            color = GoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Official Owner",
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Owner: Musadiq Hussain Jatoi",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GreenSuccess.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenSuccess)
            ) {
                Text(
                    text = "LIVE SYSTEM",
                    color = GreenSuccess,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Platform Statistics Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminMetricCard(
                title = "Total Views",
                value = "8.5M+",
                icon = Icons.Default.Visibility,
                color = GoldPrimary,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                title = "Pending Payouts",
                value = "${pendingWithdrawals.count { it.status == TransactionStatus.PENDING }}",
                icon = Icons.Default.Payments,
                color = JazzCashOrange,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                title = "Monetize Apps",
                value = "${pendingApplications.size}",
                icon = Icons.Default.Stars,
                color = BlueInfo,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                title = "Reports",
                value = "${activeReports.size}",
                icon = Icons.Default.Warning,
                color = RedAlert,
                modifier = Modifier.weight(1f)
            )
        }

        // Admin Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = LuxurySurface,
            contentColor = GoldPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                    color = GoldPrimary
                )
            }
        ) {
            Tab(
                selected = selectedAdminTab == 0,
                onClick = { selectedAdminTab = 0 },
                text = { Text("Monetization (${pendingApplications.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedAdminTab == 1,
                onClick = { selectedAdminTab = 1 },
                text = { Text("Payouts (${pendingWithdrawals.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedAdminTab == 2,
                onClick = { selectedAdminTab = 2 },
                text = { Text("Reports (${activeReports.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedAdminTab == 3,
                onClick = { selectedAdminTab = 3 },
                text = { Text("User Management", fontWeight = FontWeight.Bold) }
            )
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedAdminTab) {
                0 -> {
                    // Monetization Applications Queue
                    if (applications.isEmpty()) {
                        item { EmptyStateMessage("No monetization applications in queue.") }
                    } else {
                        items(applications, key = { it.id }) { app ->
                            AdminApplicationCard(
                                app = app,
                                onApprove = {
                                    repository.adminApproveMonetization(app.id)
                                    Toast.makeText(context, "Approved monetization for ${app.userName}", Toast.LENGTH_SHORT).show()
                                },
                                onReject = {
                                    repository.adminRejectMonetization(app.id, "Follower authenticity check did not meet 500k standard.")
                                    Toast.makeText(context, "Rejected application", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // Payment Approvals Queue
                    if (pendingWithdrawals.isEmpty()) {
                        item { EmptyStateMessage("No payout requests found.") }
                    } else {
                        items(pendingWithdrawals, key = { it.id }) { tx ->
                            AdminPayoutCard(
                                tx = tx,
                                onApprove = {
                                    repository.adminApprovePayment(tx.id)
                                    Toast.makeText(context, "Payment marked as PAID & dispatched", Toast.LENGTH_SHORT).show()
                                },
                                onReject = {
                                    repository.adminRejectPayment(tx.id)
                                    Toast.makeText(context, "Payment rejected and coins refunded", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Flagged Reports
                    if (reports.isEmpty()) {
                        item { EmptyStateMessage("All videos are compliant with community guidelines.") }
                    } else {
                        items(reports, key = { it.id }) { report ->
                            AdminReportCard(
                                report = report,
                                onTakeDown = {
                                    repository.adminResolveReport(report.id, removeVideo = true)
                                    Toast.makeText(context, "Video removed & violation recorded", Toast.LENGTH_SHORT).show()
                                },
                                onDismiss = {
                                    repository.adminResolveReport(report.id, removeVideo = false)
                                    Toast.makeText(context, "Report dismissed", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
                3 -> {
                    // User Management
                    item {
                        AdminUserCard(
                            userName = currentUser.displayName,
                            handle = "@${currentUser.username}",
                            followers = currentUser.followersCount,
                            status = if (currentUser.isMonetized) "Monetized Partner" else "Standard Creator",
                            isMonetized = currentUser.isMonetized
                        )
                    }
                    item {
                        AdminUserCard(
                            userName = "Hunza Explorer",
                            handle = "@hunza_explorer",
                            followers = 680_000,
                            status = "Monetized Partner",
                            isMonetized = true
                        )
                    }
                    item {
                        AdminUserCard(
                            userName = "Zainab Tech",
                            handle = "@zainab_tech",
                            followers = 345_000,
                            status = "Pending 500k Requirement",
                            isMonetized = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = title, color = TextMuted, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
private fun AdminApplicationCard(
    app: MonetizationApplication,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorderBright),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = app.userName, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = app.userHandle, color = GoldLight, fontSize = 12.sp)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (app.status) {
                        ApplicationStatus.APPROVED -> GreenSuccess.copy(alpha = 0.2f)
                        ApplicationStatus.PENDING -> OrangePending.copy(alpha = 0.2f)
                        ApplicationStatus.REJECTED -> RedAlert.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = app.status.title,
                        color = when (app.status) {
                            ApplicationStatus.APPROVED -> GreenSuccess
                            ApplicationStatus.PENDING -> OrangePending
                            ApplicationStatus.REJECTED -> RedAlert
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Audit Telemetry
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxuryDarkBg)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Followers: ${app.followersCount}", color = if (app.followersCount >= 500_000) GreenSuccess else RedAlert, fontSize = 11.sp)
                Text(text = "Views: ${app.verifiedViews}", color = GoldLight, fontSize = 11.sp)
                Text(text = "Watch: ${app.watchHours} hrs", color = GoldLight, fontSize = 11.sp)
                Text(text = "Strikes: ${app.strikesCount}", color = if (app.strikesCount == 0) GreenSuccess else RedAlert, fontSize = 11.sp)
            }

            if (app.status == ApplicationStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess, contentColor = TextWhite),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve Monetization", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAlert),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RedAlert),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminPayoutCard(
    tx: WalletTransaction,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${tx.paymentMethod?.title ?: "Payout"}: Rs. ${String.format("%.2f", tx.pkrAmount)}",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = tx.accountDetails, color = TextWhite, fontSize = 12.sp)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (tx.status) {
                        TransactionStatus.PAID -> GreenSuccess.copy(alpha = 0.2f)
                        TransactionStatus.PENDING -> OrangePending.copy(alpha = 0.2f)
                        else -> RedAlert.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = tx.status.label,
                        color = when (tx.status) {
                            TransactionStatus.PAID -> GreenSuccess
                            TransactionStatus.PENDING -> OrangePending
                            else -> RedAlert
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (tx.status == TransactionStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dispatched & Mark Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAlert),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RedAlert),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.6f)
                    ) {
                        Text("Reject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminReportCard(
    report: ReportTicket,
    onTakeDown: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, RedAlert.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Reported Video: ${report.videoTitle}", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Violation Reason: ${report.reason}", color = RedAlert, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Creator: ${report.creatorHandle}", color = TextMuted, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onTakeDown,
                    colors = ButtonDefaults.buttonColors(containerColor = RedAlert, contentColor = TextWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Remove Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dismiss Report", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    userName: String,
    handle: String,
    followers: Long,
    status: String,
    isMonetized: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = userName, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    if (isMonetized) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    }
                }
                Text(text = "$handle • $followers followers", color = TextMuted, fontSize = 11.sp)
                Text(text = status, color = if (isMonetized) GreenSuccess else GoldLight, fontSize = 10.sp)
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = LuxurySurfaceVariant, contentColor = TextWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Manage", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = msg, color = TextMuted, fontSize = 13.sp)
    }
}
