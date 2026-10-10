package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.TravelNwsRepository
import com.example.model.*
import androidx.activity.result.contract.ActivityResultContracts
import com.example.service.LocationService
import com.example.ui.components.AccessibilitySettingsDialog
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private lateinit var repository: TravelNwsRepository
    private lateinit var locationService: LocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = TravelNwsRepository(lifecycleScope)
        locationService = LocationService(this)

        setContent {
            val isUrdu by repository.isUrdu.collectAsStateWithLifecycle()
            val accessibilityConfig by repository.accessibilityConfig.collectAsStateWithLifecycle()

            val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val isGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (isGranted) {
                    repository.startGpsTracking(locationService)
                }
            }

            LaunchedEffect(Unit) {
                if (locationService.hasLocationPermission()) {
                    repository.startGpsTracking(locationService)
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            TravelNwsTheme(
                highContrastMode = accessibilityConfig.highContrast
            ) {
                TravelNwsApp(
                    repository = repository,
                    isUrdu = isUrdu,
                    accessibilityConfig = accessibilityConfig
                )
            }
        }
    }
}

enum class MainNavTab {
    RIDES,
    CAPTAIN_WORK,
    HISTORY,
    WALLET_PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelNwsApp(
    repository: TravelNwsRepository,
    isUrdu: Boolean,
    accessibilityConfig: AccessibilityConfig
) {
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val isOfflineMode by repository.isOfflineMode.collectAsStateWithLifecycle()
    val notificationAlert by repository.notificationAlert.collectAsStateWithLifecycle()

    val selectedPickup by repository.selectedPickup.collectAsStateWithLifecycle()
    val selectedDropoff by repository.selectedDropoff.collectAsStateWithLifecycle()
    val selectedCategory by repository.selectedCategory.collectAsStateWithLifecycle()
    val proposedFare by repository.proposedFare.collectAsStateWithLifecycle()
    val incomingBids by repository.incomingBids.collectAsStateWithLifecycle()
    val activeRide by repository.activeRide.collectAsStateWithLifecycle()
    val cachedOfflineRide by repository.cachedOfflineRide.collectAsStateWithLifecycle()
    val chatMessages by repository.chatMessages.collectAsStateWithLifecycle()

    val isDriverOnline by repository.isDriverOnline.collectAsStateWithLifecycle()
    val driverJobRequests by repository.driverJobRequests.collectAsStateWithLifecycle()
    val tripHistory by repository.tripHistory.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(MainNavTab.RIDES) }
    var showSosDialog by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    // If ride is active and assigned, switch focus to tracking tab
    val isTrackingRide = activeRide != null && (
            activeRide?.status == RideStatus.DRIVER_ASSIGNED ||
                    activeRide?.status == RideStatus.DRIVER_ARRIVING ||
                    activeRide?.status == RideStatus.IN_PROGRESS ||
                    activeRide?.status == RideStatus.COMPLETED
            )

    BackHandler(enabled = currentTab != MainNavTab.RIDES) {
        currentTab = MainNavTab.RIDES
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scaffold_travel_nws"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TRAVEL NWS",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = if (accessibilityConfig.highContrast) HighContrastYellow else NwsYellow
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NwsYellow.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (currentUser.role == UserRole.RIDER) "CAB" else "CAPTAIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (accessibilityConfig.highContrast) HighContrastYellow else NwsNavyDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isUrdu) "نواب شاہ کی اپنی سواری" else "NawabShah Rides & Bidding",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Offline indicator badge
                    if (isOfflineMode) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "OFFLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Language Toggle (English <-> Urdu)
                    TextButton(
                        onClick = { repository.toggleLanguage() },
                        modifier = Modifier.testTag("btn_toggle_lang")
                    ) {
                        Text(
                            text = if (isUrdu) "English" else "اردو",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Accessibility Dialog Shortcut
                    IconButton(
                        onClick = { showAccessibilityDialog = true },
                        modifier = Modifier.testTag("btn_top_accessibility")
                    ) {
                        Icon(
                            Icons.Default.Accessibility,
                            contentDescription = "Accessibility Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Dedicated Quick-Access SOS Button
                    IconButton(
                        onClick = { showSosDialog = true },
                        modifier = Modifier.testTag("btn_top_sos")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NwsRedSos,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = "Quick SOS",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == MainNavTab.RIDES,
                    onClick = { currentTab = MainNavTab.RIDES },
                    icon = {
                        Icon(
                            if (isTrackingRide) Icons.Default.Navigation else Icons.Default.DirectionsCar,
                            contentDescription = "Rides"
                        )
                    },
                    label = {
                        Text(
                            text = if (isTrackingRide)
                                (if (isUrdu) "لائیو ٹریک" else "Tracking")
                            else
                                (if (isUrdu) "سواری" else "Rides"),
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_rides")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.CAPTAIN_WORK,
                    onClick = { currentTab = MainNavTab.CAPTAIN_WORK },
                    icon = { Icon(Icons.Default.Work, contentDescription = "Captain Work") },
                    label = { Text(text = if (isUrdu) "کیپٹن کام" else "Captain", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_captain_work")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.HISTORY,
                    onClick = { currentTab = MainNavTab.HISTORY },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text(text = if (isUrdu) "ہسٹری" else "History", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_history")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.WALLET_PROFILE,
                    onClick = { currentTab = MainNavTab.WALLET_PROFILE },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                    label = { Text(text = if (isUrdu) "والٹ" else "Wallet", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_wallet")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Body Content
            when (currentTab) {
                MainNavTab.RIDES -> {
                    if (isTrackingRide && activeRide != null) {
                        ActiveRideTrackingScreen(
                            activeRide = activeRide!!,
                            chatMessages = chatMessages,
                            currentUserRole = currentUser.role,
                            walletBalance = currentUser.walletBalancePkr,
                            isUrdu = isUrdu,
                            accessibilityConfig = accessibilityConfig,
                            onSendMessage = { repository.sendChatMessage(it) },
                            onCancelRide = { repository.cancelActiveRide() },
                            onProcessPayment = { repository.processPayment(it) },
                            onSubmitRating = { stars, note -> repository.submitRating(stars, note) },
                            onTriggerSos = { repository.triggerEmergencySos(it) }
                        )
                    } else {
                        RiderHomeScreen(
                            selectedPickup = selectedPickup,
                            selectedDropoff = selectedDropoff,
                            selectedCategory = selectedCategory,
                            proposedFare = proposedFare,
                            incomingBids = incomingBids,
                            activeRide = activeRide,
                            cachedOfflineRide = cachedOfflineRide,
                            isOfflineMode = isOfflineMode,
                            isUrdu = isUrdu,
                            accessibilityConfig = accessibilityConfig,
                            onSelectPickup = { repository.setPickup(it) },
                            onSelectDropoff = { repository.setDropoff(it) },
                            onSelectCategory = { repository.setCategory(it) },
                            onAdjustFare = { repository.adjustFare(it) },
                            onRequestBids = { repository.requestBids() },
                            onCounterOffer = { repository.riderCounterOffer(it) },
                            onAcceptBid = { repository.acceptDriverOffer(it) },
                            onCancelRequest = { repository.cancelActiveRide() },
                            onOpenSos = { showSosDialog = true }
                        )
                    }
                }
                MainNavTab.CAPTAIN_WORK -> {
                    DriverModeScreen(
                        userProfile = currentUser,
                        isDriverOnline = isDriverOnline,
                        jobRequests = driverJobRequests,
                        isUrdu = isUrdu,
                        onToggleOnline = { repository.toggleDriverOnline() },
                        onCounterBid = { rideId, amount -> repository.driverCounterBid(rideId, amount) },
                        onAcceptJob = {
                            repository.driverAcceptJob(it)
                            currentTab = MainNavTab.RIDES
                        }
                    )
                }
                MainNavTab.HISTORY -> {
                    TripHistoryScreen(
                        tripHistory = tripHistory,
                        isUrdu = isUrdu,
                        onRebookTrip = {
                            // Find matching locations or set directly
                            currentTab = MainNavTab.RIDES
                        }
                    )
                }
                MainNavTab.WALLET_PROFILE -> {
                    WalletProfileScreen(
                        userProfile = currentUser,
                        isOfflineMode = isOfflineMode,
                        isUrdu = isUrdu,
                        accessibilityConfig = accessibilityConfig,
                        onToggleRole = { repository.toggleRole() },
                        onToggleOfflineMode = { repository.toggleOfflineMode() },
                        onTopUpWallet = { repository.topUpWallet(it) },
                        onUpdateAccessibility = { hc, el, sr, ot ->
                            repository.updateAccessibility(hc, el, sr, ot)
                        },
                        onOpenSos = { showSosDialog = true }
                    )
                }
            }

            // In-App Push Notification Alert Banner
            AnimatedVisibility(
                visible = notificationAlert != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NwsNavyDark.copy(alpha = 0.95f),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_push_notification")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = NwsYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = notificationAlert ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = { repository.dismissNotification() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss Notification",
                                tint = Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Emergency SOS Dialog
    if (showSosDialog) {
        SosEmergencyDialog(
            activeRide = activeRide,
            isUrdu = isUrdu,
            onDismiss = { showSosDialog = false },
            onSosTriggered = { repository.triggerEmergencySos(it) }
        )
    }

    // Accessibility Dialog
    if (showAccessibilityDialog) {
        AccessibilitySettingsDialog(
            config = accessibilityConfig,
            isUrdu = isUrdu,
            onUpdateConfig = { hc, el, sr, ot ->
                repository.updateAccessibility(hc, el, sr, ot)
            },
            onDismiss = { showAccessibilityDialog = false }
        )
    }
}
