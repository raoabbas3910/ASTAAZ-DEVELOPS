package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.NawabshahData
import com.example.model.AccessibilityConfig
import com.example.model.ActiveRide
import com.example.model.DriverOffer
import com.example.model.LocationItem
import com.example.model.VehicleCategory
import com.example.ui.components.FareBiddingSheet
import com.example.ui.components.NawabshahMapView
import com.example.ui.components.OfflineTripCard
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsNavyDark
import com.example.ui.theme.NwsRedSos
import com.example.ui.theme.NwsYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderHomeScreen(
    selectedPickup: LocationItem,
    selectedDropoff: LocationItem,
    selectedCategory: VehicleCategory,
    proposedFare: Int,
    incomingBids: List<DriverOffer>,
    activeRide: ActiveRide?,
    cachedOfflineRide: ActiveRide?,
    isOfflineMode: Boolean,
    isUrdu: Boolean,
    accessibilityConfig: AccessibilityConfig,
    onSelectPickup: (LocationItem) -> Unit,
    onSelectDropoff: (LocationItem) -> Unit,
    onSelectCategory: (VehicleCategory) -> Unit,
    onAdjustFare: (Int) -> Unit,
    onRequestBids: () -> Unit,
    onCounterOffer: (Int) -> Unit,
    onAcceptBid: (DriverOffer) -> Unit,
    onCancelRequest: () -> Unit,
    onOpenSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPickupDialog by remember { mutableStateOf(false) }
    var showDropoffDialog by remember { mutableStateOf(false) }
    var locationSearchQuery by remember { mutableStateOf("") }

    val distanceKm = remember(selectedPickup, selectedDropoff) {
        NawabshahData.calculateDistanceKm(selectedPickup, selectedDropoff)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (activeRide?.status == com.example.model.RideStatus.REQUESTING_BIDS) 240.dp else 16.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Visual Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NwsNavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_nws_hero_banner")
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.nws_hero_banner_1791613733944),
                            contentDescription = "Nawabshah Ride Hailing",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, NwsNavyDark.copy(alpha = 0.85f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NwsYellow,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = NwsNavyDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isUrdu) "نواب شاہ کی اپنی رائیڈ ایپ" else "Nawabshah's Premier Cab Service",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isUrdu) "اپنی مرضی کا کرایہ پیش کریں اور سفر کریں" else "Propose your own fare & negotiate with captains",
                                    color = NwsYellow,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Offline Mode Warning / Cached Trip Pass
            if (isOfflineMode) {
                item {
                    OfflineTripCard(
                        cachedRide = cachedOfflineRide,
                        isUrdu = isUrdu
                    )
                }
            }

            // Interactive Nawabshah Map Preview
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("card_map_preview"),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    NawabshahMapView(
                        activeRide = activeRide,
                        pickupLocation = selectedPickup,
                        dropoffLocation = selectedDropoff,
                        modifier = Modifier.fillMaxSize(),
                        highContrast = accessibilityConfig.highContrast
                    )
                }
            }

            // 1-Tap Quick Ride Presets (Accessible Shortcut for quick travel in Nawabshah)
            if (accessibilityConfig.oneTapQuickBooking) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = NwsYellow)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUrdu) "ایک کلک نواب شاہ روٹس" else "One-Tap Nawabshah Routes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onSelectPickup(NawabshahData.locations[0]) // QUEST
                                        onSelectDropoff(NawabshahData.locations[2]) // Railway Station
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("QUEST -> Station", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        onSelectPickup(NawabshahData.locations[1]) // PUMHS
                                        onSelectDropoff(NawabshahData.locations[3]) // Society Chowk
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("PUMHS -> Society", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Location Selector Box (Pickup & Drop)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_location_picker")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Pickup Location Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    locationSearchQuery = ""
                                    showPickupDialog = true
                                }
                                .padding(vertical = 6.dp)
                                .testTag("row_select_pickup"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NwsGreen,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.TripOrigin,
                                        contentDescription = "Pickup Location",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isUrdu) "کہاں سے اٹھانا ہے (Pickup):" else "Pickup Location:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isUrdu) selectedPickup.nameUrdu else selectedPickup.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = selectedPickup.address,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        // Dropoff Location Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    locationSearchQuery = ""
                                    showDropoffDialog = true
                                }
                                .padding(vertical = 6.dp)
                                .testTag("row_select_dropoff"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NwsRedSos,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Drop Location",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isUrdu) "کہاں جانا ہے (Destination):" else "Where to (Drop-off):",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isUrdu) selectedDropoff.nameUrdu else selectedDropoff.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = selectedDropoff.address,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }

            // Vehicle Category Selector
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) "گاڑی کی قسم منتخب کریں:" else "Select Ride Option:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "$distanceKm km (~${(distanceKm * 3.5).toInt().coerceAtLeast(3)} mins)",
                            fontSize = 12.sp,
                            color = NwsGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(NawabshahData.vehicleCategories, key = { it.id }) { cat ->
                            val isSelected = selectedCategory.id == cat.id
                            val estimatedFare = NawabshahData.estimateFare(cat, distanceKm)

                            Card(
                                modifier = Modifier
                                    .width(145.dp)
                                    .clickable { onSelectCategory(cat) }
                                    .testTag("card_vehicle_${cat.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, NwsYellow) else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = when (cat.iconType) {
                                            "MOTO" -> Icons.Default.TwoWheeler
                                            "RICKSHAW" -> Icons.Default.ElectricRickshaw
                                            "MINI" -> Icons.Default.DirectionsCar
                                            else -> Icons.Default.AirportShuttle
                                        },
                                        contentDescription = cat.name,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(28.dp)
                                    )

                                    Text(
                                        text = if (isUrdu) cat.nameUrdu else cat.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = cat.subtitle,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = "~Rs. $estimatedFare",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Fare Offer Proposer & Argue Bar
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("card_propose_fare")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isUrdu) "اپنا کرایہ پیش کریں (Argue Fare):" else "Offer Your Desired Fare:",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Rs. $proposedFare",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalButton(
                                    onClick = { onAdjustFare(-20) },
                                    modifier = Modifier.testTag("btn_adjust_minus_20")
                                ) {
                                    Text("-20", fontWeight = FontWeight.Bold)
                                }
                                FilledTonalButton(
                                    onClick = { onAdjustFare(20) },
                                    modifier = Modifier.testTag("btn_adjust_plus_20")
                                ) {
                                    Text("+20", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Action Button (Start inDrive / Yango style Bidding)
                        Button(
                            onClick = onRequestBids,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NwsYellow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_find_cab")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NwsNavyDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "سواری تلاش کریں (Find Captain)" else "Find Captain (Offer Rs. $proposedFare)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NwsNavyDark
                            )
                        }
                    }
                }
            }
        }

        // Bidding Bottom Sheet Overlay when rider is waiting for bids
        if (activeRide != null && activeRide.status == com.example.model.RideStatus.REQUESTING_BIDS) {
            FareBiddingSheet(
                proposedFare = proposedFare,
                incomingBids = incomingBids,
                isUrdu = isUrdu,
                onAdjustFare = onAdjustFare,
                onCounterOffer = onCounterOffer,
                onAcceptBid = onAcceptBid,
                onCancelRequest = onCancelRequest,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Pickup Location Picker Dialog
    if (showPickupDialog) {
        LocationSearchDialog(
            title = if (isUrdu) "پک اپ لوکیشن منتخب کریں" else "Select Pickup in Nawabshah",
            locations = NawabshahData.locations,
            onSelect = {
                onSelectPickup(it)
                showPickupDialog = false
            },
            onDismiss = { showPickupDialog = false }
        )
    }

    // Dropoff Location Picker Dialog
    if (showDropoffDialog) {
        LocationSearchDialog(
            title = if (isUrdu) "منزل (ڈراپ آف) منتخب کریں" else "Select Drop-off in Nawabshah",
            locations = NawabshahData.locations,
            onSelect = {
                onSelectDropoff(it)
                showDropoffDialog = false
            },
            onDismiss = { showDropoffDialog = false }
        )
    }
}

@Composable
fun LocationSearchDialog(
    title: String,
    locations: List<LocationItem>,
    onSelect: (LocationItem) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) locations
        else locations.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.nameUrdu.contains(searchQuery, ignoreCase = true) ||
                    it.address.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_location_search"),
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search landmark (QUEST, PUMHS, Chowk)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_nawabshah_landmark")
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered, key = { it.id }) { loc ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(loc) }
                                .testTag("item_location_${loc.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    tint = NwsYellow,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = loc.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = loc.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
