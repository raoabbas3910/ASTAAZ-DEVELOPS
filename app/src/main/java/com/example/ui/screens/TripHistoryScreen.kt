package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TripHistoryItem
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsRedSos
import com.example.ui.theme.NwsYellow

@Composable
fun TripHistoryScreen(
    tripHistory: List<TripHistoryItem>,
    isUrdu: Boolean,
    onRebookTrip: (TripHistoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTripForReceipt by remember { mutableStateOf<TripHistoryItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isUrdu) "سابقہ سفر (Trip History)" else "Trip History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = if (isUrdu) "آپ کے نواب شاہ میں مکمل شدہ اسفار" else "Your completed journeys in Nawabshah",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${tripHistory.size}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        if (tripHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isUrdu) "ابھی تک کوئی سفر مکمل نہیں ہوا" else "No completed trips yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tripHistory, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_history_${item.id}"),
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
                                Text(
                                    text = item.dateString,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Rs. ${item.farePkr}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Divider()

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TripOrigin, contentDescription = null, tint = NwsGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = item.pickupName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = NwsRedSos, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = item.dropoffName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Divider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${item.driverName} • ${item.plateNumber}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB703), modifier = Modifier.size(14.dp))
                                        Text(text = " Rated ${item.ratingGiven}★", fontSize = 11.sp)
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { selectedTripForReceipt = item },
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(text = if (isUrdu) "رسید" else "Receipt", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onRebookTrip(item) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NwsYellow),
                                        contentPadding = PaddingValues(horizontal = 10.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(
                                            text = if (isUrdu) "دوبارہ بک کریں" else "Re-book",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Digital Tax Invoice / Receipt Dialog
    if (selectedTripForReceipt != null) {
        val trip = selectedTripForReceipt!!
        AlertDialog(
            onDismissRequest = { selectedTripForReceipt = null },
            modifier = Modifier.testTag("dialog_trip_receipt"),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = NwsGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "E-Receipt (TRAVEL NWS)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Invoice ID: NWS-INV-${trip.id.takeLast(6)}", fontSize = 11.sp, color = Color.Gray)
                    Text(text = "Date: ${trip.dateString}", fontSize = 12.sp)
                    Divider()
                    Text(text = "Route: ${trip.pickupName} -> ${trip.dropoffName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Vehicle: ${trip.vehicleTypeName}", fontSize = 12.sp)
                    Text(text = "Captain: ${trip.driverName} (${trip.plateNumber})", fontSize = 12.sp)
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Base Distance Fare:", fontSize = 12.sp)
                        Text(text = "Rs. ${(trip.farePkr * 0.9).toInt()}", fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Safety & Platform Fee:", fontSize = 12.sp)
                        Text(text = "Rs. ${(trip.farePkr * 0.1).toInt()}", fontSize = 12.sp)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total Paid (${trip.paymentMethod.displayName}):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Rs. ${trip.farePkr}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = NwsGreen)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareText = "TRAVEL NWS E-Receipt\nTrip: ${trip.pickupName} -> ${trip.dropoffName}\nFare: Rs. ${trip.farePkr}\nCaptain: ${trip.driverName}"
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Receipt"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Share Receipt")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTripForReceipt = null }) {
                    Text("Close")
                }
            }
        )
    }
}
