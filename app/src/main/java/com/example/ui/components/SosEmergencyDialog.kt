package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveRide
import com.example.ui.theme.NwsRedSos
import kotlinx.coroutines.delay

@Composable
fun SosEmergencyDialog(
    activeRide: ActiveRide?,
    isUrdu: Boolean,
    onDismiss: () -> Unit,
    onSosTriggered: (String) -> Unit
) {
    val context = LocalContext.current
    var isSirenPlaying by remember { mutableStateOf(false) }
    var sirenFlashState by remember { mutableStateOf(false) }

    val flashColor by animateColorAsState(
        targetValue = if (sirenFlashState) Color(0xFFFF2222) else Color(0xFF1E293B),
        label = "sirenFlash"
    )

    // Flash screen when siren active
    LaunchedEffect(isSirenPlaying) {
        while (isSirenPlaying) {
            sirenFlashState = !sirenFlashState
            delay(400)
        }
        sirenFlashState = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_sos_emergency"),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = NwsRedSos,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "SOS Warning",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = if (isUrdu) "ہنگامی مدد (SOS)" else "Emergency Safety (SOS)",
                    fontWeight = FontWeight.Bold,
                    color = NwsRedSos,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isUrdu)
                        "اگر آپ کو سفر کے دوران کوئی خطرہ محسوس ہو، فوری طور پر نیچے دیے گئے بٹن استعمال کریں۔ آپ کا لائیو لوکیشن اور گاڑی کا نمبر درج ذیل ہے۔"
                    else
                        "Instant emergency access for Nawabshah riders & captains. Your live location and vehicle details will be shared.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Current trip info banner
                if (activeRide != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Ride: #${activeRide.rideId} | Driver: ${activeRide.assignedDriver?.driverName ?: "En route"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Plate: ${activeRide.assignedDriver?.plateNumber ?: "N/A"} | Current: ${activeRide.pickup.name}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Call 15 Nawabshah Police
                Button(
                    onClick = {
                        onSosTriggered("Madadgar 15 Police")
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NwsRedSos),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_call_police_15")
                ) {
                    Icon(Icons.Default.LocalPolice, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "پولیس مددگار 15 کال کریں" else "Call 15 Police (Nawabshah)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Call 1122 Sindh Rescue
                OutlinedButton(
                    onClick = {
                        onSosTriggered("Sindh Rescue 1122")
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1122"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_call_rescue_1122")
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = NwsRedSos)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "ریسکیو 1122 کال کریں" else "Call 1122 Sindh Rescue",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Share Live Ride Info with Family
                Button(
                    onClick = {
                        onSosTriggered("Shared Live Ride Link")
                        val rideDetails = "TRAVEL NWS Emergency Alert!\nI am currently in ride #${activeRide?.rideId ?: "NWS-TRIP"}\n" +
                                "Driver: ${activeRide?.assignedDriver?.driverName ?: "Captain"} (${activeRide?.assignedDriver?.plateNumber ?: "Plate"})\n" +
                                "Route: ${activeRide?.pickup?.name} -> ${activeRide?.dropoff?.name}\n" +
                                "Emergency Helpline: 15 / 1122 Nawabshah"
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, rideDetails)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Ride Details"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_live_ride")
                ) {
                    Icon(Icons.Default.ShareLocation, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "فیملی کے ساتھ لائیو سفر شیئر کریں" else "Share Live Ride with Family",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Loud Siren Alarm
                Button(
                    onClick = {
                        isSirenPlaying = !isSirenPlaying
                        if (isSirenPlaying) onSosTriggered("Loud Siren Alarm Activated")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = flashColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_toggle_siren")
                ) {
                    Icon(
                        imageVector = if (isSirenPlaying) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSirenPlaying)
                            (if (isUrdu) "سائرن بند کریں" else "Stop Loud Siren")
                        else
                            (if (isUrdu) "بلند آواز سائرن بجائیں" else "Trigger Loud Siren Alarm"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    isSirenPlaying = false
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_close_sos")
            ) {
                Text(text = if (isUrdu) "بند کریں" else "Close")
            }
        }
    )
}
