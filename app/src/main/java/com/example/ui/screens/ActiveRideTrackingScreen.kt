package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ActiveRideTrackingScreen(
    activeRide: ActiveRide,
    chatMessages: List<ChatMessage>,
    currentUserRole: UserRole,
    walletBalance: Int,
    isUrdu: Boolean,
    accessibilityConfig: AccessibilityConfig,
    onSendMessage: (String) -> Unit,
    onCancelRide: () -> Unit,
    onProcessPayment: (PaymentMethod) -> Unit,
    onSubmitRating: (Float, String) -> Unit,
    onTriggerSos: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showChatSheet by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(activeRide.status == RideStatus.COMPLETED) }

    LaunchedEffect(activeRide.status) {
        if (activeRide.status == RideStatus.COMPLETED) {
            showRatingDialog = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Map fills entire background
        NawabshahMapView(
            activeRide = activeRide,
            pickupLocation = activeRide.pickup,
            dropoffLocation = activeRide.dropoff,
            modifier = Modifier.fillMaxSize(),
            highContrast = accessibilityConfig.highContrast
        )

        // Floating Top Header (Status banner + Quick SOS access)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Chip
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NwsNavyDark.copy(alpha = 0.95f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                when (activeRide.status) {
                                    RideStatus.DRIVER_ASSIGNED, RideStatus.DRIVER_ARRIVING -> NwsYellow
                                    RideStatus.IN_PROGRESS -> NwsGreen
                                    RideStatus.COMPLETED -> NwsGreen
                                    else -> Color.Gray
                                },
                                CircleShape
                            )
                    )
                    Text(
                        text = when (activeRide.status) {
                            RideStatus.DRIVER_ASSIGNED -> if (isUrdu) "کیپٹن آرہا ہے (${activeRide.estimatedMinutes} منٹ)" else "Captain on the way (${activeRide.estimatedMinutes} min)"
                            RideStatus.DRIVER_ARRIVING -> if (isUrdu) "کیپٹن پہنچ چکا ہے" else "Captain Arrived at Pickup"
                            RideStatus.IN_PROGRESS -> if (isUrdu) "سفر جاری ہے" else "Trip in Progress to ${activeRide.dropoff.name}"
                            RideStatus.COMPLETED -> if (isUrdu) "سفر مکمل ہو گیا" else "Trip Completed! Pay Fare"
                            else -> "NWS Ride"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Dedicated Quick-Access SOS Button
            FloatingActionButton(
                onClick = { showSosDialog = true },
                shape = CircleShape,
                containerColor = NwsRedSos,
                contentColor = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_quick_access_sos")
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = "Emergency SOS Quick Access",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Bottom Driver Information & Control Card
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("card_active_ride_bottom"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Trip Progress bar
                if (activeRide.status == RideStatus.IN_PROGRESS) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Trip Progress",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(activeRide.currentProgress * 100).toInt()}% (${activeRide.estimatedMinutes} min remaining)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NwsGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { activeRide.currentProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = NwsGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                // Driver Profile & Vehicle Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NwsYellow,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = activeRide.assignedDriver?.driverName?.firstOrNull()?.toString() ?: "D",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = NwsNavyDark
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeRide.assignedDriver?.driverName ?: "Captain",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified Captain", tint = NwsGreen, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${activeRide.assignedDriver?.vehicleModel} • ${activeRide.assignedDriver?.plateNumber}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB703), modifier = Modifier.size(14.dp))
                                Text(
                                    text = " ${activeRide.assignedDriver?.rating ?: 4.9} (${activeRide.assignedDriver?.totalTrips ?: 400}+ trips)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Safety Verification PIN
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.border(1.dp, NwsYellow, RoundedCornerShape(10.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "SAFETY PIN", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = activeRide.pinCode,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fare: Rs. ${activeRide.agreedFare}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Interactive Action Row (Call, Chat, Payment)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Call Captain
                    Button(
                        onClick = {
                            val phone = activeRide.assignedDriver?.phone ?: "03001234567"
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_call_captain"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isUrdu) "کال کریں" else "Call", fontSize = 12.sp)
                    }

                    // Chat with Captain
                    Button(
                        onClick = { showChatSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_chat"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isUrdu) "چیٹ" else "Chat", fontSize = 12.sp)
                    }

                    // Pay Now / Gateways
                    Button(
                        onClick = { showPaymentDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeRide.isPaid) NwsGreen else NwsYellow
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("btn_open_payment"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (activeRide.isPaid) Icons.Default.CheckCircle else Icons.Default.Payment,
                            contentDescription = null,
                            tint = if (activeRide.isPaid) Color.White else NwsNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (activeRide.isPaid)
                                (if (isUrdu) "ادا شدہ" else "Paid ✓")
                            else
                                (if (isUrdu) "ادائیگی" else "Pay Rs.${activeRide.agreedFare}"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeRide.isPaid) Color.White else NwsNavyDark
                        )
                    }
                }

                // Cancel Ride link if not yet completed
                if (activeRide.status != RideStatus.COMPLETED) {
                    Text(
                        text = if (isUrdu) "سواری منسوخ کریں" else "Cancel Ride",
                        color = NwsRedSos,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onCancelRide() }
                            .padding(vertical = 4.dp)
                            .testTag("btn_cancel_active_ride")
                    )
                }
            }
        }

        // In-Ride Chat Sheet
        if (showChatSheet) {
            InRideChatSheet(
                activeRide = activeRide,
                chatMessages = chatMessages,
                currentUserRole = currentUserRole,
                isUrdu = isUrdu,
                onSendMessage = onSendMessage,
                onCloseChat = { showChatSheet = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Emergency SOS Dialog
        if (showSosDialog) {
            SosEmergencyDialog(
                activeRide = activeRide,
                isUrdu = isUrdu,
                onDismiss = { showSosDialog = false },
                onSosTriggered = onTriggerSos
            )
        }

        // Payment Gateway Dialog
        if (showPaymentDialog) {
            PaymentGatewayDialog(
                activeRide = activeRide,
                walletBalance = walletBalance,
                isUrdu = isUrdu,
                onConfirmPayment = {
                    onProcessPayment(it)
                    showPaymentDialog = false
                },
                onDismiss = { showPaymentDialog = false }
            )
        }

        // Rating Dialog (Shown after ride completion)
        if (showRatingDialog && activeRide.status == RideStatus.COMPLETED) {
            RatingFeedbackDialog(
                activeRide = activeRide,
                isUrdu = isUrdu,
                onSubmitRating = { stars, feedback ->
                    onSubmitRating(stars, feedback)
                    showRatingDialog = false
                },
                onDismiss = { showRatingDialog = false }
            )
        }
    }
}
