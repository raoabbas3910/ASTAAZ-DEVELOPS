package com.example.data

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class TravelNwsRepository(private val coroutineScope: CoroutineScope) {

    // Current User Role: RIDER or DRIVER
    private val _currentUser = MutableStateFlow(UserProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Language setting
    private val _isUrdu = MutableStateFlow(false)
    val isUrdu: StateFlow<Boolean> = _isUrdu.asStateFlow()

    // Accessibility settings
    private val _accessibilityConfig = MutableStateFlow(AccessibilityConfig())
    val accessibilityConfig: StateFlow<AccessibilityConfig> = _accessibilityConfig.asStateFlow()

    // Offline mode toggle (supports offline trip caching)
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    // Push notification alerts
    private val _notificationAlert = MutableStateFlow<String?>(null)
    val notificationAlert: StateFlow<String?> = _notificationAlert.asStateFlow()

    // Pickup & Drop locations
    private val _selectedPickup = MutableStateFlow(NawabshahData.locations[0]) // QUEST
    val selectedPickup: StateFlow<LocationItem> = _selectedPickup.asStateFlow()

    private val _selectedDropoff = MutableStateFlow(NawabshahData.locations[3]) // Society Chowk
    val selectedDropoff: StateFlow<LocationItem> = _selectedDropoff.asStateFlow()

    private val _selectedCategory = MutableStateFlow(NawabshahData.vehicleCategories[0]) // Moto
    val selectedCategory: StateFlow<VehicleCategory> = _selectedCategory.asStateFlow()

    // Proposed fare by rider
    private val _proposedFare = MutableStateFlow(90)
    val proposedFare: StateFlow<Int> = _proposedFare.asStateFlow()

    // Available driver bids (for fare negotiation)
    private val _incomingBids = MutableStateFlow<List<DriverOffer>>(emptyList())
    val incomingBids: StateFlow<List<DriverOffer>> = _incomingBids.asStateFlow()

    // Active Ride
    private val _activeRide = MutableStateFlow<ActiveRide?>(null)
    val activeRide: StateFlow<ActiveRide?> = _activeRide.asStateFlow()

    // Cached Offline Ride (persisted even when offline)
    private val _cachedOfflineRide = MutableStateFlow<ActiveRide?>(null)
    val cachedOfflineRide: StateFlow<ActiveRide?> = _cachedOfflineRide.asStateFlow()

    // Real-time Chat Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Driver Mode Duty Status & Nearby Ride Requests
    private val _isDriverOnline = MutableStateFlow(true)
    val isDriverOnline: StateFlow<Boolean> = _isDriverOnline.asStateFlow()

    private val _driverJobRequests = MutableStateFlow<List<ActiveRide>>(emptyList())
    val driverJobRequests: StateFlow<List<ActiveRide>> = _driverJobRequests.asStateFlow()

    // Trip History
    private val _liveGpsLocation = MutableStateFlow<android.location.Location?>(null)
    val liveGpsLocation: StateFlow<android.location.Location?> = _liveGpsLocation.asStateFlow()

    private val _tripHistory = MutableStateFlow<List<TripHistoryItem>>(
        listOf(
            TripHistoryItem(
                id = "trip_hist_1",
                dateString = "Yesterday, 05:40 PM",
                pickupName = "PUMHS Medical University",
                dropoffName = "Nawabshah Railway Station",
                vehicleTypeName = "NWS Mini (Alto)",
                farePkr = 260,
                driverName = "Captain Farhan Sindhi",
                plateNumber = "NWS-MH-109",
                ratingGiven = 5.0f,
                paymentMethod = PaymentMethod.JAZZCASH
            ),
            TripHistoryItem(
                id = "trip_hist_2",
                dateString = "07 Oct 2026, 09:15 AM",
                pickupName = "Society Chowk",
                dropoffName = "Civil Hospital Nawabshah",
                vehicleTypeName = "Auto / Qingqi",
                farePkr = 120,
                driverName = "Allah Ditta",
                plateNumber = "NWS-AUTO-290",
                ratingGiven = 4.5f,
                paymentMethod = PaymentMethod.CASH
            )
        )
    )
    val tripHistory: StateFlow<List<TripHistoryItem>> = _tripHistory.asStateFlow()

    // Simulation job for tracking movement
    private var trackingJob: Job? = null

    init {
        recalculateFare()
        generateSampleDriverJobRequests()
    }

    fun toggleRole() {
        val newRole = if (_currentUser.value.role == UserRole.RIDER) UserRole.DRIVER else UserRole.RIDER
        _currentUser.value = _currentUser.value.copy(role = newRole)
        showNotification(
            if (newRole == UserRole.DRIVER) "Switched to Driver Mode (Captain) 🚖"
            else "Switched to Rider Mode 🙋‍♂️"
        )
    }

    fun toggleLanguage() {
        _isUrdu.value = !_isUrdu.value
    }

    fun updateAccessibility(highContrast: Boolean? = null, enlargedText: Boolean? = null, screenReader: Boolean? = null, oneTap: Boolean? = null) {
        val current = _accessibilityConfig.value
        _accessibilityConfig.value = current.copy(
            highContrast = highContrast ?: current.highContrast,
            enlargedText = enlargedText ?: current.enlargedText,
            screenReaderAnnouncements = screenReader ?: current.screenReaderAnnouncements,
            oneTapQuickBooking = oneTap ?: current.oneTapQuickBooking
        )
    }

    fun toggleOfflineMode() {
        val newVal = !_isOfflineMode.value
        _isOfflineMode.value = newVal
        showNotification(if (newVal) "Offline Mode Activated. Displaying Cached Trips." else "Connected to Live NWS Network.")
    }

    fun setPickup(location: LocationItem) {
        _selectedPickup.value = location
        recalculateFare()
    }

    fun setDropoff(location: LocationItem) {
        _selectedDropoff.value = location
        recalculateFare()
    }

    fun setCategory(category: VehicleCategory) {
        _selectedCategory.value = category
        recalculateFare()
    }

    fun adjustFare(delta: Int) {
        val minFare = (_selectedCategory.value.baseFarePkr * 0.8).toInt()
        val current = _proposedFare.value
        val updated = (current + delta).coerceAtLeast(minFare)
        _proposedFare.value = updated
    }

    private fun recalculateFare() {
        val dist = NawabshahData.calculateDistanceKm(_selectedPickup.value, _selectedDropoff.value)
        val calculated = NawabshahData.estimateFare(_selectedCategory.value, dist)
        _proposedFare.value = calculated
    }

    // Step 1: Rider posts request and starts waiting for Captain Bids (inDrive/Yango style negotiation)
    fun requestBids() {
        _incomingBids.value = emptyList()
        val dist = NawabshahData.calculateDistanceKm(_selectedPickup.value, _selectedDropoff.value)
        val estMin = (dist * 3.5).toInt().coerceAtLeast(4)

        val ride = ActiveRide(
            rideId = "NWS-${Random.nextInt(10000, 99999)}",
            pickup = _selectedPickup.value,
            dropoff = _selectedDropoff.value,
            vehicleCategory = _selectedCategory.value,
            riderProposedFare = _proposedFare.value,
            agreedFare = _proposedFare.value,
            status = RideStatus.REQUESTING_BIDS,
            assignedDriver = null,
            pinCode = String.format("%04d", Random.nextInt(1000, 9999)),
            distanceKm = dist,
            estimatedMinutes = estMin,
            paymentMethod = PaymentMethod.CASH,
            isPaid = false
        )
        _activeRide.value = ride
        _cachedOfflineRide.value = ride
        showNotification("Ride request broadcasted to nearby captains in Nawabshah!")

        // Simulate incoming bids with realistic delay
        coroutineScope.launch {
            delay(1200)
            val bids = NawabshahData.generateMockDriverBids(_selectedCategory.value, _proposedFare.value)
            _incomingBids.value = bids
            showNotification("${bids.size} Captains offered fares for your ride!")
        }
    }

    // Counter offer by Rider to Captains
    fun riderCounterOffer(newFare: Int) {
        _proposedFare.value = newFare
        val currentRide = _activeRide.value ?: return
        _activeRide.value = currentRide.copy(riderProposedFare = newFare, agreedFare = newFare)
        showNotification("Counter offer Rs. $newFare sent to captains!")

        coroutineScope.launch {
            delay(1500)
            // Some drivers might adjust their bid
            val updatedBids = _incomingBids.value.mapIndexed { idx, offer ->
                if (idx == 0) offer.copy(proposedFare = newFare) // accepts rider's counter!
                else offer
            }
            _incomingBids.value = updatedBids
            showNotification("Captain ${updatedBids[0].driverName} agreed to Rs. $newFare!")
        }
    }

    // Step 2: Rider accepts a Captain's bid
    fun acceptDriverOffer(offer: DriverOffer) {
        val current = _activeRide.value ?: return
        val updatedRide = current.copy(
            assignedDriver = offer,
            agreedFare = offer.proposedFare,
            status = RideStatus.DRIVER_ASSIGNED,
            driverLat = offer.currentLat,
            driverLng = offer.currentLng
        )
        _activeRide.value = updatedRide
        _cachedOfflineRide.value = updatedRide
        _incomingBids.value = emptyList()

        // Initialize Chat with greeting
        _chatMessages.value = listOf(
            ChatMessage(
                id = "chat_welcome",
                senderRole = UserRole.DRIVER,
                text = "Assalam-o-Alaikum! Main ${offer.driverName} hon. Location ki taraf aa raha hon.",
                timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            )
        )

        showNotification("Captain ${offer.driverName} (${offer.vehicleModel}) is on the way! ETA: ${offer.etaMinutes} mins")
        startDriverSimulation()
    }

    // Real-time tracking simulation: Driver moving towards pickup, then towards destination
    private fun startDriverSimulation() {
        trackingJob?.cancel()
        trackingJob = coroutineScope.launch {
            val pickup = _activeRide.value?.pickup ?: return@launch
            val dropoff = _activeRide.value?.dropoff ?: return@launch

            // Phase 1: Driver arriving (0% to 100% of approach)
            _activeRide.value = _activeRide.value?.copy(status = RideStatus.DRIVER_ARRIVING)
            showNotification("Captain arriving at ${_activeRide.value?.pickup?.name}...")

            for (step in 1..5) {
                delay(3000)
                val ride = _activeRide.value ?: break
                if (ride.status == RideStatus.CANCELLED || ride.status == RideStatus.COMPLETED) break
                val fraction = step / 5f
                val driverLat = pickup.lat - 0.008 * (1f - fraction)
                val driverLng = pickup.lng - 0.008 * (1f - fraction)
                _activeRide.value = ride.copy(
                    driverLat = driverLat,
                    driverLng = driverLng,
                    estimatedMinutes = (5 - step).coerceAtLeast(1)
                )
            }

            // Driver has arrived!
            val rideAfterArrival = _activeRide.value ?: return@launch
            if (rideAfterArrival.status == RideStatus.CANCELLED) return@launch

            showNotification("Captain has arrived at your pickup spot!")
            delay(3000)

            // Phase 2: Trip Started (In Progress towards dropoff)
            _activeRide.value = _activeRide.value?.copy(status = RideStatus.IN_PROGRESS)
            showNotification("Ride started to ${dropoff.name} 🏁")

            val steps = 10
            for (step in 1..steps) {
                delay(2500)
                val current = _activeRide.value ?: break
                if (current.status == RideStatus.CANCELLED) break
                val progress = step.toFloat() / steps
                val currentLat = pickup.lat + (dropoff.lat - pickup.lat) * progress
                val currentLng = pickup.lng + (dropoff.lng - pickup.lng) * progress
                val remainingMins = ((1f - progress) * current.distanceKm * 3.5).toInt().coerceAtLeast(1)

                _activeRide.value = current.copy(
                    currentProgress = progress,
                    driverLat = currentLat,
                    driverLng = currentLng,
                    estimatedMinutes = remainingMins
                )
            }

            // Trip Complete
            val finalRide = _activeRide.value ?: return@launch
            if (finalRide.status == RideStatus.IN_PROGRESS) {
                _activeRide.value = finalRide.copy(
                    status = RideStatus.COMPLETED,
                    currentProgress = 1f
                )
                showNotification("Alhamdulillah! You have reached your destination. Please pay Rs. ${finalRide.agreedFare}")

                // Add to history
                val hist = TripHistoryItem(
                    id = "trip_${System.currentTimeMillis()}",
                    dateString = "Just Now",
                    pickupName = finalRide.pickup.name,
                    dropoffName = finalRide.dropoff.name,
                    vehicleTypeName = finalRide.vehicleCategory.name,
                    farePkr = finalRide.agreedFare,
                    driverName = finalRide.assignedDriver?.driverName ?: "Captain NWS",
                    plateNumber = finalRide.assignedDriver?.plateNumber ?: "SBN-123",
                    ratingGiven = 5f,
                    paymentMethod = finalRide.paymentMethod
                )
                _tripHistory.value = listOf(hist) + _tripHistory.value
                _cachedOfflineRide.value = finalRide
            }
        }
    }

    // Cancel Active Ride
    fun cancelActiveRide(reason: String = "Rider request") {
        trackingJob?.cancel()
        _activeRide.value = _activeRide.value?.copy(status = RideStatus.CANCELLED)
        showNotification("Ride cancelled: $reason")
        coroutineScope.launch {
            delay(1500)
            _activeRide.value = null
        }
    }

    // Complete Payment
    fun processPayment(method: PaymentMethod) {
        val ride = _activeRide.value ?: return
        if (method == PaymentMethod.NWS_WALLET) {
            val balance = _currentUser.value.walletBalancePkr
            if (balance >= ride.agreedFare) {
                _currentUser.value = _currentUser.value.copy(walletBalancePkr = balance - ride.agreedFare)
            }
        }
        _activeRide.value = ride.copy(paymentMethod = method, isPaid = true)
        showNotification("Payment of Rs. ${ride.agreedFare} completed via ${method.displayName}!")
    }

    fun submitRating(stars: Float, feedback: String) {
        showNotification("Thank you! Rated Captain $stars ★")
        _activeRide.value = null
    }

    // In-Ride Chat
    fun sendChatMessage(text: String) {
        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val msg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderRole = _currentUser.value.role,
            text = text,
            timestamp = timeNow
        )
        _chatMessages.value = _chatMessages.value + msg

        // Simulated Captain reply after rider message
        if (_currentUser.value.role == UserRole.RIDER) {
            coroutineScope.launch {
                delay(2000)
                val replyText = when {
                    text.contains("bahir", ignoreCase = true) -> "Theek hai sir, main bilkul samne hon."
                    text.contains("chillar", ignoreCase = true) || text.contains("khulay", ignoreCase = true) -> "Ji haan, khulay paise hain mere paas."
                    text.contains("kahan", ignoreCase = true) -> "Signal cross kar raha hon, 1 minute mein hazir."
                    else -> "Ji janab, theek hai! Main dekh raha hon."
                }
                val captainReply = ChatMessage(
                    id = "msg_rep_${System.currentTimeMillis()}",
                    senderRole = UserRole.DRIVER,
                    text = replyText,
                    timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                )
                _chatMessages.value = _chatMessages.value + captainReply
            }
        }
    }

    // Emergency SOS Trigger
    fun triggerEmergencySos(type: String) {
        showNotification("🚨 SOS TRIGGERED: $type reported to Nawabshah 15 Police & Live Location Shared!")
    }

    // Wallet Top-up
    fun topUpWallet(amount: Int) {
        _currentUser.value = _currentUser.value.copy(
            walletBalancePkr = _currentUser.value.walletBalancePkr + amount
        )
        showNotification("Rs. $amount added to NWS Wallet!")
    }

    // Driver Mode Functions
    fun toggleDriverOnline() {
        _isDriverOnline.value = !_isDriverOnline.value
        showNotification(if (_isDriverOnline.value) "You are now ONLINE. Searching Nawabshah rides..." else "You are now OFFLINE.")
    }

    fun driverCounterBid(rideId: String, counterFare: Int) {
        showNotification("Counter bid of Rs. $counterFare placed on Ride $rideId!")
        // Update job
        _driverJobRequests.value = _driverJobRequests.value.map { job ->
            if (job.rideId == rideId) job.copy(agreedFare = counterFare) else job
        }
    }

    fun driverAcceptJob(job: ActiveRide) {
        showNotification("Ride #${job.rideId} accepted! Navigating to ${job.pickup.name}...")
        _activeRide.value = job.copy(status = RideStatus.DRIVER_ARRIVING)
        _driverJobRequests.value = _driverJobRequests.value.filter { it.rideId != job.rideId }
    }

    private fun generateSampleDriverJobRequests() {
        _driverJobRequests.value = listOf(
            ActiveRide(
                rideId = "REQ-7102",
                pickup = NawabshahData.locations[1], // PUMHS
                dropoff = NawabshahData.locations[4], // Taj Colony
                vehicleCategory = NawabshahData.vehicleCategories[2], // Mini
                riderProposedFare = 240,
                agreedFare = 240,
                status = RideStatus.REQUESTING_BIDS,
                assignedDriver = null,
                pinCode = "4819",
                distanceKm = 3.2,
                estimatedMinutes = 8,
                paymentMethod = PaymentMethod.CASH,
                isPaid = false
            ),
            ActiveRide(
                rideId = "REQ-8429",
                pickup = NawabshahData.locations[0], // QUEST
                dropoff = NawabshahData.locations[2], // Railway Station
                vehicleCategory = NawabshahData.vehicleCategories[0], // Moto
                riderProposedFare = 90,
                agreedFare = 90,
                status = RideStatus.REQUESTING_BIDS,
                assignedDriver = null,
                pinCode = "1923",
                distanceKm = 4.1,
                estimatedMinutes = 10,
                paymentMethod = PaymentMethod.JAZZCASH,
                isPaid = false
            ),
            ActiveRide(
                rideId = "REQ-9351",
                pickup = NawabshahData.locations[3], // Society Chowk
                dropoff = NawabshahData.locations[8], // National Highway Bypass
                vehicleCategory = NawabshahData.vehicleCategories[3], // Sedan AC
                riderProposedFare = 450,
                agreedFare = 450,
                status = RideStatus.REQUESTING_BIDS,
                assignedDriver = null,
                pinCode = "6612",
                distanceKm = 6.4,
                estimatedMinutes = 14,
                paymentMethod = PaymentMethod.EASYPAISA,
                isPaid = false
            )
        )
    }

    fun startGpsTracking(locationService: com.example.service.LocationService) {
        if (!locationService.hasLocationPermission()) return
        coroutineScope.launch {
            try {
                locationService.getLocationUpdates(intervalMillis = 3000L).collect { loc ->
                    _liveGpsLocation.value = loc
                    val currentRide = _activeRide.value
                    if (currentRide != null) {
                        if (_currentUser.value.role == UserRole.DRIVER) {
                            _activeRide.value = currentRide.copy(
                                driverLat = loc.latitude,
                                driverLng = loc.longitude
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Silently handle if permission revoked
            }
        }
    }

    fun dismissNotification() {
        _notificationAlert.value = null
    }

    private fun showNotification(msg: String) {
        _notificationAlert.value = msg
    }
}
