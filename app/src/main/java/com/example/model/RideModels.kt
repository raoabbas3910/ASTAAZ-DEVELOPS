package com.example.model

enum class RideStatus {
    IDLE,
    REQUESTING_BIDS,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum class UserRole {
    RIDER,
    DRIVER
}

enum class PaymentMethod(val displayName: String, val displayNameUrdu: String) {
    CASH("Cash to Driver", "ڈرائیور کو نقد"),
    JAZZCASH("JazzCash Mobile", "جاز کیش"),
    EASYPAISA("Easypaisa Account", "ایزی پیسہ"),
    NWS_WALLET("NWS In-App Wallet", "ٹریول این ڈبلیو ایس والٹ"),
    CARD("Debit / Credit Card", "بینک کارڈ")
}

data class LocationItem(
    val id: String,
    val name: String,
    val nameUrdu: String,
    val category: String, // University, Transport, Hospital, Market, Colony, etc.
    val address: String,
    val lat: Double,
    val lng: Double
)

data class VehicleCategory(
    val id: String,
    val name: String,
    val nameUrdu: String,
    val subtitle: String,
    val capacity: Int,
    val baseFarePkr: Int,
    val perKmRatePkr: Int,
    val iconType: String // "MOTO", "RICKSHAW", "MINI", "SEDAN"
)

data class DriverOffer(
    val id: String,
    val driverId: String,
    val driverName: String,
    val phone: String,
    val rating: Float,
    val totalTrips: Int,
    val vehicleModel: String,
    val vehicleColor: String,
    val plateNumber: String,
    val vehicleType: String,
    val proposedFare: Int,
    val etaMinutes: Int,
    val distanceAwayKm: Double,
    val currentLat: Double,
    val currentLng: Double
)

data class ActiveRide(
    val rideId: String,
    val pickup: LocationItem,
    val dropoff: LocationItem,
    val vehicleCategory: VehicleCategory,
    val riderProposedFare: Int,
    val agreedFare: Int,
    val status: RideStatus,
    val assignedDriver: DriverOffer?,
    val pinCode: String,
    val distanceKm: Double,
    val estimatedMinutes: Int,
    val paymentMethod: PaymentMethod,
    val isPaid: Boolean,
    val currentProgress: Float = 0f, // 0.0 to 1.0 along route
    val driverLat: Double = 0.0,
    val driverLng: Double = 0.0,
    val startTimeMillis: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val senderRole: UserRole,
    val text: String,
    val timestamp: String,
    val isDelivered: Boolean = true
)

data class TripHistoryItem(
    val id: String,
    val dateString: String,
    val pickupName: String,
    val dropoffName: String,
    val vehicleTypeName: String,
    val farePkr: Int,
    val driverName: String,
    val plateNumber: String,
    val ratingGiven: Float,
    val paymentMethod: PaymentMethod
)

data class UserProfile(
    val name: String = "Abbas Rao",
    val phone: String = "+92 300 1234567",
    val email: String = "raoabbas3910@gmail.com",
    val role: UserRole = UserRole.RIDER,
    val walletBalancePkr: Int = 1850,
    val driverLicenseVerified: Boolean = true,
    val driverVehicle: String = "Honda City 1.3 (NWS-7721)",
    val driverRating: Float = 4.92f,
    val driverTotalRides: Int = 412
)

data class AccessibilityConfig(
    val highContrast: Boolean = false,
    val enlargedText: Boolean = false,
    val screenReaderAnnouncements: Boolean = false,
    val oneTapQuickBooking: Boolean = false
)
