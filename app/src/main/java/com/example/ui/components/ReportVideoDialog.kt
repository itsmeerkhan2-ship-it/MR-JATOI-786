package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.*

@Composable
fun ReportVideoDialog(
    videoId: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmitReport: (String) -> Unit
) {
    val reportReasons = listOf(
        AppStrings.get("report_reason_spam", language),
        AppStrings.get("report_reason_violence", language),
        AppStrings.get("report_reason_hate", language),
        AppStrings.get("report_reason_adult", language),
        AppStrings.get("report_reason_copyright", language)
    )

    var selectedReason by remember { mutableStateOf(reportReasons[0]) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = LuxurySurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorderBright),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RedAlert,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("report", language),
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Text(
                    text = "Help keep MR JATOI VIDEO community safe. Select a reason for flagging this video:",
                    color = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    reportReasons.forEach { reason ->
                        val isSelected = selectedReason == reason
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LuxurySurfaceVariant else LuxuryDarkBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) GoldPrimary else LuxuryBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedReason = reason }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GoldPrimary,
                                    unselectedColor = TextMuted
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reason,
                                color = if (isSelected) GoldLight else TextWhite,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = { onSubmitReport(selectedReason) },
                    colors = ButtonDefaults.buttonColors(containerColor = RedAlert),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_report_button")
                ) {
                    Text(
                        text = AppStrings.get("submit_report", language),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
