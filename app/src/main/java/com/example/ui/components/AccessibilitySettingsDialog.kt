package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessibilityConfig
import com.example.ui.theme.NwsYellow

@Composable
fun AccessibilitySettingsDialog(
    config: AccessibilityConfig,
    isUrdu: Boolean,
    onUpdateConfig: (highContrast: Boolean?, enlargedText: Boolean?, screenReader: Boolean?, oneTap: Boolean?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_accessibility_settings"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Accessibility,
                    contentDescription = null,
                    tint = NwsYellow,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "خصوصی افراد کے لیے سہولیات" else "Accessibility Mode (Easy Access)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isUrdu)
                        "معذور اور کم نظر افراد کی آسانی کے لیے خصوصی فیچرز:"
                    else
                        "Enhancements for visually impaired and mobility-assisted riders and captains:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. High-Contrast Dark Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) "ہائی کنٹراسٹ موڈ (High Contrast)" else "High Contrast Mode",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isUrdu) "گہرے سیاہ بیک گراؤنڈ پر واضح زرد رنگ" else "Vivid yellow on deep black for low vision",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.highContrast,
                        onCheckedChange = { onUpdateConfig(it, null, null, null) },
                        modifier = Modifier.testTag("switch_high_contrast")
                    )
                }

                Divider()

                // 2. Large Typography & Touch Targets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) "بڑا فونٹ اور بڑے بٹن" else "Large Text & Big Touch Targets",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isUrdu) "پڑھنے اور کلک کرنے میں آسانی" else "Enlarge touch area to 56dp+ for easy tapping",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.enlargedText,
                        onCheckedChange = { onUpdateConfig(null, it, null, null) },
                        modifier = Modifier.testTag("switch_large_text")
                    )
                }

                Divider()

                // 3. Spoken Status / TalkBack helper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) "صوتی بیانات (Voice Readout)" else "Voice Announcements",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isUrdu) "ڈرائیور کے آنے پر آواز سے آگاہی" else "Audible voice prompt when driver approaches",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.screenReaderAnnouncements,
                        onCheckedChange = { onUpdateConfig(null, null, it, null) },
                        modifier = Modifier.testTag("switch_voice_readout")
                    )
                }

                Divider()

                // 4. One-Tap Quick Booking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) "ایک کلک بکنگ (One-Tap Fast Ride)" else "One-Tap Quick Ride Presets",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isUrdu) "بغیر ٹائپنگ یونیورسٹی یا ہسپتال کا سفر" else "Instant 1-tap book for QUEST / Hospital / Station",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.oneTapQuickBooking,
                        onCheckedChange = { onUpdateConfig(null, null, null, it) },
                        modifier = Modifier.testTag("switch_one_tap_booking")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_close_accessibility")
            ) {
                Text(text = if (isUrdu) "محفوظ کریں" else "Save & Apply")
            }
        }
    )
}
