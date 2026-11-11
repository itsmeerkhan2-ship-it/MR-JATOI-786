package com.example.ui.screens.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
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
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.WalletTransaction
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    repository: AppRepository,
    language: AppLanguage,
    onNavigateToMonetization: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by repository.currentUser.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val dailyWatchCoins by repository.dailyWatchCoinsEarned.collectAsState()

    var showWithdrawModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Wallet Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = AppStrings.get("wallet", language),
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "MR JATOI VIDEO Official Earning & Payout Center",
                    color = GoldLight,
                    fontSize = 12.sp
                )
            }
        }

        // Luxury Black & Gold Balance Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LuxuryBlack),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, LuxuryBorderBright),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    LuxuryBlack,
                                    GoldContainer.copy(alpha = 0.8f),
                                    LuxurySurface
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.get("wallet_balance", language),
                                    color = GoldLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Exchange Rate Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = LuxuryBlack.copy(alpha = 0.7f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder)
                            ) {
                                Text(
                                    text = AppStrings.get("coin_rate", language),
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Coins Balance
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${currentUser.coins}",
                                color = TextWhite,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Coins",
                                color = GoldPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        // PKR Equivalent
                        Text(
                            text = "≈ ${String.format("%.2f", currentUser.pkrBalance)} PKR",
                            color = GreenSuccess,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons: Withdraw & Monetization
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showWithdrawModal = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = LuxuryBlack
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("request_withdrawal_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Withdraw",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onNavigateToMonetization,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Monetize",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Watch-to-Earn Progress Tracker
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today's Watch Rewards",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "$dailyWatchCoins / 50 Coins",
                            color = GoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (dailyWatchCoins / 50f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldPrimary,
                        trackColor = LuxuryDarkBg
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Anti-fraud rule: Minimum 10 seconds continuous watch required per video reward.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Daily Streak Bonus Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = JazzCashOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${AppStrings.get("daily_streak", language)} (Day ${currentUser.streakDays})",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Claim +25 Coins daily bonus!",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            val claimed = repository.claimDailyStreak()
                            if (claimed) {
                                Toast.makeText(context, "Claimed 25 Coins Daily Bonus!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Today's bonus already claimed!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !currentUser.lastStreakClaimed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = LuxuryBlack,
                            disabledContainerColor = LuxurySurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (currentUser.lastStreakClaimed) "Claimed" else "Claim",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Referral Code Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppStrings.get("referral_code", language),
                        color = GoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = AppStrings.get("referral_desc", language),
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(LuxuryDarkBg)
                            .border(1.dp, LuxuryBorderBright, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentUser.referralCode,
                            color = GoldPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = AppStrings.get("copy_code", language),
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Referral Code", currentUser.referralCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, AppStrings.get("code_copied", language), Toast.LENGTH_SHORT).show()
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Security & Anti-Fraud Notice
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LuxuryBlack),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.get("compliance_notice", language),
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Transactions Header
        item {
            Text(
                text = "Transaction History (${transactions.size})",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
            )
        }

        // Transactions List
        items(transactions, key = { it.id }) { tx ->
            TransactionRowItem(tx = tx)
        }
    }

    // Withdrawal Modal
    if (showWithdrawModal) {
        WithdrawalBottomSheet(
            currentBalanceCoins = currentUser.coins,
            currentBalancePkr = currentUser.pkrBalance,
            language = language,
            onDismiss = { showWithdrawModal = false },
            onSubmit = { method, title, number, bank, pkr ->
                val (success, message) = repository.requestWithdrawal(method, title, number, bank, pkr)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                if (success) {
                    showWithdrawModal = false
                }
            }
        )
    }
}

@Composable
private fun TransactionRowItem(tx: WalletTransaction) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (tx.type) {
                                TransactionType.WITHDRAWAL -> RedAlert.copy(alpha = 0.2f)
                                TransactionType.DAILY_STREAK -> JazzCashOrange.copy(alpha = 0.2f)
                                else -> GoldPrimary.copy(alpha = 0.2f)
                            }
                        )
                ) {
                    Icon(
                        imageVector = when (tx.type) {
                            TransactionType.WITHDRAWAL -> Icons.Default.ArrowOutward
                            TransactionType.DAILY_STREAK -> Icons.Default.LocalFireDepartment
                            TransactionType.REFERRAL_BONUS -> Icons.Default.GroupAdd
                            else -> Icons.Default.MonetizationOn
                        },
                        contentDescription = null,
                        tint = if (tx.type == TransactionType.WITHDRAWAL) RedAlert else GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tx.type.label,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tx.referenceNote,
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    if (tx.accountDetails.isNotBlank()) {
                        Text(
                            text = tx.accountDetails,
                            color = GoldLight,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val isWithdrawal = tx.type == TransactionType.WITHDRAWAL
                Text(
                    text = "${if (isWithdrawal) "-" else "+"}${tx.coins} Coins",
                    color = if (isWithdrawal) RedAlert else GoldPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rs. ${String.format("%.2f", tx.pkrAmount)}",
                    color = TextWhite,
                    fontSize = 11.sp
                )

                // Status Chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (tx.status) {
                        TransactionStatus.APPROVED -> GreenSuccess.copy(alpha = 0.2f)
                        TransactionStatus.PAID -> GreenSuccess.copy(alpha = 0.3f)
                        TransactionStatus.PENDING -> OrangePending.copy(alpha = 0.2f)
                        TransactionStatus.REJECTED -> RedAlert.copy(alpha = 0.2f)
                    },
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = tx.status.label,
                        color = when (tx.status) {
                            TransactionStatus.APPROVED, TransactionStatus.PAID -> GreenSuccess
                            TransactionStatus.PENDING -> OrangePending
                            TransactionStatus.REJECTED -> RedAlert
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawalBottomSheet(
    currentBalanceCoins: Long,
    currentBalancePkr: Double,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (PaymentMethod, String, String, String, Double) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.JAZZCASH) }
    var accountTitle by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var amountPkrText by remember { mutableStateOf("500") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LuxurySurface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get("withdraw_funds", language),
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Text(
                text = AppStrings.get("min_withdrawal", language),
                color = GoldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Method Selector
            Text(
                text = AppStrings.get("payment_method", language),
                color = GoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GoldPrimary else LuxuryDarkBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else LuxuryBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMethod = method }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = method.title,
                                color = if (isSelected) LuxuryBlack else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = method.badge,
                                color = if (isSelected) LuxuryBlack.copy(alpha = 0.8f) else TextMuted,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Account Title
            OutlinedTextField(
                value = accountTitle,
                onValueChange = { accountTitle = it },
                label = { Text(AppStrings.get("account_title", language)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = LuxuryBorder
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("account_title_input")
            )

            // Account / Mobile Number
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text(AppStrings.get("account_number", language)) },
                placeholder = {
                    Text(if (selectedMethod == PaymentMethod.JAZZCASH || selectedMethod == PaymentMethod.EASYPAISA) "03XX-XXXXXXX" else "IBAN / Wallet Address")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = LuxuryBorder
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("account_number_input")
            )

            if (selectedMethod == PaymentMethod.BANK_ACCOUNT) {
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text(AppStrings.get("bank_name", language)) },
                    placeholder = { Text("e.g. HBL, Meezan, UBL, Alfalah") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LuxuryBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Amount PKR
            OutlinedTextField(
                value = amountPkrText,
                onValueChange = { amountPkrText = it },
                label = { Text(AppStrings.get("amount_pkr", language)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = LuxuryBorder
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("withdrawal_amount_input")
            )

            Button(
                onClick = {
                    val pkr = amountPkrText.toDoubleOrNull() ?: 0.0
                    onSubmit(selectedMethod, accountTitle.trim(), accountNumber.trim(), bankName.trim(), pkr)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_withdrawal_button")
            ) {
                Text(
                    text = AppStrings.get("submit_withdrawal", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
