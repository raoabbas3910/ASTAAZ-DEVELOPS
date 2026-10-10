package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessibilityConfig
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.AccessibilitySettingsDialog
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsNavyDark
import com.example.ui.theme.NwsYellow

@Composable
fun WalletProfileScreen(
    userProfile: UserProfile,
    isOfflineMode: Boolean,
    isUrdu: Boolean,
    accessibilityConfig: AccessibilityConfig,
    onToggleRole: () -> Unit,
    onToggleOfflineMode: () -> Unit,
    onTopUpWallet: (Int) -> Unit,
    onUpdateAccessibility: (Boolean?, Boolean?, Boolean?, Boolean?) -> Unit,
    onOpenSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTopUpDialog by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NwsNavyDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_user_profile")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NwsYellow,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userProfile.name.first().toString(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = NwsNavyDark
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = NwsGreen, modifier = Modifier.size(16.dp))
                            }
                            Text(text = userProfile.phone, fontSize = 12.sp, color = Color.LightGray)
                            Text(text = userProfile.email, fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    // Role Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NwsYellow
                    ) {
                        Text(
                            text = if (userProfile.role == UserRole.RIDER) "RIDER" else "CAPTAIN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NwsNavyDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Divider(color = Color.DarkGray)

                // Role Switcher Button (Switch to Rider or Captain)
                Button(
                    onClick = onToggleRole,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_toggle_role")
                ) {
                    Icon(
                        imageVector = if (userProfile.role == UserRole.RIDER) Icons.Default.DirectionsCar else Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (userProfile.role == UserRole.RIDER)
                            (if (isUrdu) "ڈرائیور موڈ میں تبدیل کریں (Switch to Captain)" else "Switch to Captain / Work Mode")
                        else
                            (if (isUrdu) "رائیڈر موڈ میں تبدیل کریں (Switch to Rider)" else "Switch to Rider Mode"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Wallet Balance Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth().testTag("card_wallet_balance")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isUrdu) "ٹریول نواب شاہ والٹ بیلنس:" else "NWS Wallet Balance:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Rs. ${userProfile.walletBalancePkr}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Button(
                    onClick = { showTopUpDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NwsYellow),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_top_up_wallet")
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = NwsNavyDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "ریچارج" else "Top-up",
                        fontWeight = FontWeight.Bold,
                        color = NwsNavyDark
                    )
                }
            }
        }

        // Settings & Options List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Offline Mode Toggle
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (isOfflineMode) Color(0xFF64748B) else Color.Gray
                            )
                            Column {
                                Text(
                                    text = if (isUrdu) "آف لائن ٹرپ موڈ" else "Offline Mode",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isUrdu) "انٹرنیٹ کے بغیر ٹرپ ڈیٹا دیکھیں" else "Access essential ride details without internet",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isOfflineMode,
                            onCheckedChange = { onToggleOfflineMode() },
                            modifier = Modifier.testTag("switch_offline_mode")
                        )
                    }
                }
            }

            // Accessibility Settings Item
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAccessibilityDialog = true }
                        .testTag("item_accessibility_settings")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Accessibility, contentDescription = null, tint = NwsYellow)
                            Column {
                                Text(
                                    text = if (isUrdu) "خصوصی افراد کے لیے سہولیات" else "Accessibility Mode",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isUrdu) "ہائی کنٹراسٹ، بڑا فونٹ، صوتی بیانات" else "High contrast, large fonts & voice readout",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            // Emergency Safety Hotline Item
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSos() }
                        .testTag("item_emergency_sos_hotlines")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.LocalPolice, contentDescription = null, tint = Color(0xFFEF4444))
                            Column {
                                Text(
                                    text = if (isUrdu) "ہنگامی مدد اور ہیلپ لائن (15 / 1122)" else "Emergency Hotlines (15 Police / 1122)",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isUrdu) "سندھ ریسکیو اور نواب شاہ پولیس مددگار" else "Nawabshah Police & Sindh Rescue quick dispatch",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }
        }
    }

    // Top-up Dialog (JazzCash / EasyPaisa / Card)
    if (showTopUpDialog) {
        var topUpAmount by remember { mutableStateOf(500) }
        AlertDialog(
            onDismissRequest = { showTopUpDialog = false },
            modifier = Modifier.testTag("dialog_top_up_wallet"),
            title = {
                Text(text = if (isUrdu) "والٹ ریچارج کریں" else "Top-up NWS Wallet", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isUrdu) "رقم منتخب کریں (جاز کیش / ایزی پیسہ سے فوری ٹرانسفر):" else "Select amount to add via JazzCash / Easypaisa:",
                        fontSize = 13.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(300, 500, 1000).forEach { amt ->
                            FilledTonalButton(
                                onClick = { topUpAmount = amt },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (topUpAmount == amt) NwsYellow else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Rs. $amt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTopUpWallet(topUpAmount)
                        showTopUpDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_top_up")
                ) {
                    Text(text = "Recharge Rs. $topUpAmount")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopUpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Accessibility Dialog
    if (showAccessibilityDialog) {
        AccessibilitySettingsDialog(
            config = accessibilityConfig,
            isUrdu = isUrdu,
            onUpdateConfig = onUpdateAccessibility,
            onDismiss = { showAccessibilityDialog = false }
        )
    }
}
