package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.ActiveRide
import com.example.model.UserProfile
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsNavyDark
import com.example.ui.theme.NwsRedSos
import com.example.ui.theme.NwsYellow

@Composable
fun DriverModeScreen(
    userProfile: UserProfile,
    isDriverOnline: Boolean,
    jobRequests: List<ActiveRide>,
    isUrdu: Boolean,
    onToggleOnline: () -> Unit,
    onCounterBid: (rideId: String, amount: Int) -> Unit,
    onAcceptJob: (ActiveRide) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedJobForCounter by remember { mutableStateOf<ActiveRide?>(null) }
    var counterAmountInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Driver Duty Status & Earnings Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NwsNavyDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_driver_header")
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NwsYellow,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userProfile.name.first().toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = NwsNavyDark
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Captain ${userProfile.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified Driver",
                                    tint = NwsGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = userProfile.driverVehicle,
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    // Online / Offline Switch
                    Switch(
                        checked = isDriverOnline,
                        onCheckedChange = { onToggleOnline() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NwsGreen,
                            checkedTrackColor = NwsGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("switch_driver_duty")
                    )
                }

                Divider(color = Color.DarkGray)

                // Today's Stats in Nawabshah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Today's Earnings", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "Rs. 4,850", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NwsYellow)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Trips Done", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "12", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Driver Rating", fontSize = 11.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = NwsYellow, modifier = Modifier.size(16.dp))
                            Text(text = " ${userProfile.driverRating}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Active Job Requests in Nawabshah Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isUrdu) "نواب شاہ میں نئی سواریاں (Live Requests):" else "Available Ride Orders in Nawabshah:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDriverOnline) NwsGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
            ) {
                Text(
                    text = if (isDriverOnline) "ONLINE" else "OFFLINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDriverOnline) NwsGreen else Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        if (!isDriverOnline) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isUrdu) "آپ اس وقت آف لائن ہیں" else "You are currently OFFLINE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isUrdu) "نواب شاہ کی سواریوں کے آرڈرز دیکھنے کے لیے آن لائن بٹن دبائیں۔" else "Toggle duty switch above to start receiving ride requests and argue fares.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else if (jobRequests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NwsYellow)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isUrdu) "نئی سواریوں کی تلاش جاری ہے..." else "Searching for passengers in Nawabshah...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(jobRequests, key = { it.rideId }) { job ->
                    DriverJobRequestCard(
                        job = job,
                        isUrdu = isUrdu,
                        onCounterClick = {
                            selectedJobForCounter = job
                            counterAmountInput = (job.agreedFare + 40).toString()
                        },
                        onAcceptClick = { onAcceptJob(job) }
                    )
                }
            }
        }
    }

    // Driver Counter-Fare Dialog (Argue Fare)
    if (selectedJobForCounter != null) {
        val job = selectedJobForCounter!!
        AlertDialog(
            onDismissRequest = { selectedJobForCounter = null },
            modifier = Modifier.testTag("dialog_driver_counter_fare"),
            title = {
                Text(
                    text = if (isUrdu) "مسافر کو اپنا کرایہ پیش کریں" else "Argue / Counter Fare to Passenger",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Rider offered: Rs. ${job.riderProposedFare} (${job.pickup.name} -> ${job.dropoff.name})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "Your counter fare in PKR:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = counterAmountInput,
                        onValueChange = { counterAmountInput = it.filter { c -> c.isDigit() } },
                        leadingIcon = { Text("Rs. ") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_driver_counter_fare")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { counterAmountInput = (job.agreedFare + 20).toString() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+20")
                        }
                        FilledTonalButton(
                            onClick = { counterAmountInput = (job.agreedFare + 50).toString() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+50")
                        }
                        FilledTonalButton(
                            onClick = { counterAmountInput = (job.agreedFare + 100).toString() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+100")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = counterAmountInput.toIntOrNull() ?: (job.agreedFare + 40)
                        onCounterBid(job.rideId, amount)
                        selectedJobForCounter = null
                    },
                    modifier = Modifier.testTag("btn_submit_driver_counter")
                ) {
                    Text(text = if (isUrdu) "آفر بھیجیں" else "Send Offer")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedJobForCounter = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DriverJobRequestCard(
    job: ActiveRide,
    isUrdu: Boolean,
    onCounterClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_driver_job_${job.rideId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${job.vehicleCategory.name} • ${job.distanceKm} km",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "Rs. ${job.agreedFare}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Route points
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TripOrigin, contentDescription = null, tint = NwsGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = job.pickup.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = NwsRedSos, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = job.dropoff.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // Action Buttons (Argue / Accept)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCounterClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_driver_argue_${job.rideId}")
                ) {
                    Icon(Icons.Default.PriceChange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isUrdu) "کرایہ بڑھائیں" else "Argue Fare")
                }

                Button(
                    onClick = onAcceptClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NwsGreen),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("btn_driver_accept_${job.rideId}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isUrdu) "قبول کریں" else "Accept (Rs. ${job.agreedFare})", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
