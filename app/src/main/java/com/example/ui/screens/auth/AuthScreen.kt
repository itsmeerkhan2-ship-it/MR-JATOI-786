package com.example.ui.screens.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    repository: AppRepository,
    language: AppLanguage,
    onAuthSuccess: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPhoneMode by remember { mutableStateOf(true) }
    var phoneInput by remember { mutableStateOf("+92 312 9876543") }
    var emailInput by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(60) }
    var isVerifying by remember { mutableStateOf(false) }

    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryBlack.copy(alpha = 0.95f))
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LuxurySurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, LuxuryBorderBright),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MR JATOI VIDEO",
                        color = GoldPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GoldContainer)
                        .border(1.5.dp, GoldPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isOtpSent) Icons.Default.Lock else Icons.Default.Person,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isOtpSent) AppStrings.get("enter_otp", language) else AppStrings.get("login_register", language),
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isOtpSent) "Verification code sent to ${if (isPhoneMode) phoneInput else emailInput}" else "Watch Videos • Create Videos • Earn Money",
                    color = TextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isOtpSent) {
                    // Registration Mode Toggle: Phone vs Email
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(LuxuryDarkBg)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPhoneMode) GoldPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isPhoneMode = true }
                        ) {
                            Text(
                                text = "Mobile Phone",
                                color = if (isPhoneMode) LuxuryBlack else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isPhoneMode) GoldPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isPhoneMode = false }
                        ) {
                            Text(
                                text = "Email Address",
                                color = if (!isPhoneMode) LuxuryBlack else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isPhoneMode) {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text(AppStrings.get("phone_number", language)) },
                            leadingIcon = {
                                Text(
                                    text = "🇵🇰",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                )
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
                                .testTag("auth_phone_input")
                        )
                    } else {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text(AppStrings.get("email_address", language)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = GoldPrimary)
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
                                .testTag("auth_email_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (isPhoneMode && phoneInput.isBlank()) {
                                Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isPhoneMode && emailInput.isBlank()) {
                                Toast.makeText(context, "Please enter email address", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isOtpSent = true
                            Toast.makeText(context, "OTP Code: 786-000 (Simulated for instant verification)", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("send_otp_button")
                    ) {
                        Text(
                            text = AppStrings.get("send_otp", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // OTP Entry
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { if (it.length <= 6) otpInput = it },
                        label = { Text("Enter 6-digit Code (e.g. 786000)") },
                        placeholder = { Text("786000") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = LuxuryBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_code_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (countdown > 0) "Resend in ${countdown}s" else "Didn't receive code?",
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                        if (countdown == 0) {
                            Text(
                                text = AppStrings.get("resend_otp", language),
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    countdown = 60
                                    Toast.makeText(context, "New OTP sent!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isVerifying = true
                            Toast.makeText(context, "Login verified successfully! Welcome to MR JATOI VIDEO.", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_otp_button")
                    ) {
                        Text(
                            text = AppStrings.get("verify_continue", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
