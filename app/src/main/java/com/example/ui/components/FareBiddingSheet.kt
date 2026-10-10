package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DriverOffer
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsNavyDark
import com.example.ui.theme.NwsRedSos
import com.example.ui.theme.NwsYellow

@Composable
fun FareBiddingSheet(
    proposedFare: Int,
    incomingBids: List<DriverOffer>,
    isUrdu: Boolean,
    onAdjustFare: (Int) -> Unit,
    onCounterOffer: (Int) -> Unit,
    onAcceptBid: (DriverOffer) -> Unit,
    onCancelRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCounterDialog by remember { mutableStateOf(false) }
    var customFareInput by remember { mutableStateOf(proposedFare.toString()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_fare_bidding_sheet"),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Drag handle / Title header
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isUrdu) "کرایہ پر بات چیت (Fare Bidding)" else "Fare Negotiation & Bids",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isUrdu) "نواب شاہ کے کیپٹنز سے پیشکش موصول ہو رہی ہیں" else "Receiving offers from Nawabshah captains",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = onCancelRequest,
                    colors = ButtonDefaults.textButtonColors(contentColor = NwsRedSos),
                    modifier = Modifier.testTag("btn_cancel_request")
                ) {
                    Text(text = if (isUrdu) "کینسل کریں" else "Cancel")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Rider Proposed Fare Display with quick adjustments
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isUrdu) "آپ کا تجویز کردہ کرایہ:" else "Your Offered Fare:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Rs. $proposedFare",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Quick +/- buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { onAdjustFare(-20) },
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_fare_minus_20")
                        ) {
                            Text("-20", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        FilledTonalButton(
                            onClick = { onAdjustFare(20) },
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_fare_plus_20")
                        ) {
                            Text("+20", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        FilledTonalButton(
                            onClick = { onAdjustFare(50) },
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_fare_plus_50")
                        ) {
                            Text("+50", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Incoming Captain Bids Section
            Text(
                text = if (incomingBids.isEmpty())
                    (if (isUrdu) "کیپٹنز کی جانب سے آفرز کا انتظار ہے..." else "Waiting for Captain offers in Nawabshah...")
                else
                    (if (isUrdu) "${incomingBids.size} کیپٹن تیار ہیں (انتخاب کریں):" else "${incomingBids.size} Captain Offers Available:"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (incomingBids.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = NwsYellow,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isUrdu) "قریبی گاڑیوں کو ریکوئسٹ بھیجی جا رہی ہے..." else "Broadcasting your fare to nearby captains...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(incomingBids, key = { it.id }) { offer ->
                        DriverBidCard(
                            offer = offer,
                            isUrdu = isUrdu,
                            onAccept = { onAcceptBid(offer) },
                            onCounter = {
                                customFareInput = (offer.proposedFare - 20).toString()
                                showCounterDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCounterDialog) {
        AlertDialog(
            onDismissRequest = { showCounterDialog = false },
            title = {
                Text(text = if (isUrdu) "نیا کرایہ پیش کریں" else "Counter Offer Fare")
            },
            text = {
                Column {
                    Text(
                        text = if (isUrdu) "اپنا مطلوبہ کرایہ روپے میں لکھیں:" else "Enter your proposed fare in PKR:",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        valValue = customFareInput,
                        onValueChange = { customFareInput = it.filter { char -> char.isDigit() } },
                        leadingIcon = { Text("Rs. ") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_counter_fare")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customFareInput.toIntOrNull() ?: proposedFare
                        onCounterOffer(amount)
                        showCounterDialog = false
                    },
                    modifier = Modifier.testTag("btn_submit_counter")
                ) {
                    Text(text = if (isUrdu) "آفر بھیجیں" else "Send Offer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCounterDialog = false }) {
                    Text(text = if (isUrdu) "منسوخ" else "Cancel")
                }
            }
        )
    }
}

// Helper Composable to wrap String into TextFieldValue or text
@Composable
private fun OutlinedTextField(
    valValue: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.OutlinedTextField(
        value = valValue,
        onValueChange = onValueChange,
        leadingIcon = leadingIcon,
        singleLine = singleLine,
        modifier = modifier
    )
}

@Composable
fun DriverBidCard(
    offer: DriverOffer,
    isUrdu: Boolean,
    onAccept: () -> Unit,
    onCounter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_driver_bid_${offer.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Driver info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = NwsYellow,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = offer.driverName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = NwsNavyDark
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = offer.driverName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB703),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${offer.rating}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "${offer.vehicleModel} (${offer.plateNumber})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "ETA: ${offer.etaMinutes} min • ${offer.distanceAwayKm} km",
                        fontSize = 11.sp,
                        color = NwsGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Fare & Accept / Argue buttons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Rs. ${offer.proposedFare}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onCounter,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_counter_bid_${offer.id}")
                    ) {
                        Text(
                            text = if (isUrdu) "بحث" else "Argue",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = NwsGreen),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_accept_bid_${offer.id}")
                    ) {
                        Text(
                            text = if (isUrdu) "قبول" else "Accept",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
