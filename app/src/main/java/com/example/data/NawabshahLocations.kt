package com.example.data

import com.example.model.DriverOffer
import com.example.model.LocationItem
import com.example.model.VehicleCategory
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object NawabshahData {

    // Vehicles available in Nawabshah
    val vehicleCategories = listOf(
        VehicleCategory(
            id = "moto",
            name = "NWS Moto",
            nameUrdu = "موٹر سائیکل",
            subtitle = "Tez & Kifayati 1 Sawaar",
            capacity = 1,
            baseFarePkr = 60,
            perKmRatePkr = 25,
            iconType = "MOTO"
        ),
        VehicleCategory(
            id = "rickshaw",
            name = "Auto / Qingqi",
            nameUrdu = "آٹو رکشہ / چنگچی",
            subtitle = "Family & Local Bazaars",
            capacity = 3,
            baseFarePkr = 100,
            perKmRatePkr = 35,
            iconType = "RICKSHAW"
        ),
        VehicleCategory(
            id = "mini",
            name = "NWS Mini (Alto/Mehran)",
            nameUrdu = "منی کار",
            subtitle = "Economy Budget Car",
            capacity = 4,
            baseFarePkr = 220,
            perKmRatePkr = 55,
            iconType = "MINI"
        ),
        VehicleCategory(
            id = "sedan",
            name = "NWS Sedan AC (Corolla/Yaris)",
            nameUrdu = "سیڈان اے سی",
            subtitle = "Chilled AC VIP Ride",
            capacity = 4,
            baseFarePkr = 350,
            perKmRatePkr = 75,
            iconType = "SEDAN"
        )
    )

    // Authentic Nawabshah landmarks with real geographic coordinates
    val locations = listOf(
        LocationItem(
            id = "quest",
            name = "QUEST University",
            nameUrdu = "قائد عوام انجینئرنگ یونیورسٹی",
            category = "Education",
            address = "Sakrand Road, Nawabshah",
            lat = 26.2420,
            lng = 68.3980
        ),
        LocationItem(
            id = "pumhs",
            name = "PUMHS Medical University",
            nameUrdu = "پیپلز میڈیکل یونیورسٹی فار ویمن",
            category = "Education",
            address = "Hospital Road, Nawabshah",
            lat = 26.2490,
            lng = 68.4120
        ),
        LocationItem(
            id = "railway_station",
            name = "Nawabshah Railway Station",
            nameUrdu = "نواب شاہ ریلوے اسٹیشن",
            category = "Transport",
            address = "Station Road, City Center",
            lat = 26.2450,
            lng = 68.4160
        ),
        LocationItem(
            id = "society_chowk",
            name = "Society Chowk",
            nameUrdu = "سوسائٹی چوک",
            category = "City Center",
            address = "Main VIP Road / Court Road Intersection",
            lat = 26.2515,
            lng = 68.4075
        ),
        LocationItem(
            id = "taj_colony",
            name = "Taj Colony",
            nameUrdu = "تاج کالونی",
            category = "Residential",
            address = "Near Municipal Park, Nawabshah",
            lat = 26.2570,
            lng = 68.4110
        ),
        LocationItem(
            id = "civil_hospital",
            name = "Civil Hospital Nawabshah",
            nameUrdu = "سول ہسپتال نواب شاہ",
            category = "Healthcare",
            address = "Court Road, Nawabshah",
            lat = 26.2475,
            lng = 68.4135
        ),
        LocationItem(
            id = "press_club",
            name = "Nawabshah Press Club",
            nameUrdu = "پریس کلب نواب شاہ",
            category = "Media",
            address = "Circuit House Road",
            lat = 26.2540,
            lng = 68.4040
        ),
        LocationItem(
            id = "airport_nws",
            name = "Nawabshah Airport",
            nameUrdu = "نواب شاہ ایئرپورٹ",
            category = "Transport",
            address = "Airport Road, Outskirts",
            lat = 26.2230,
            lng = 68.3840
        ),
        LocationItem(
            id = "bypass_mor",
            name = "National Highway Bypass Mor",
            nameUrdu = "بائی پاس موڑ (قومی شاہراہ)",
            category = "Highway",
            address = "N-5 National Highway Link",
            lat = 26.2700,
            lng = 68.4250
        ),
        LocationItem(
            id = "kazi_ahmed_mor",
            name = "Kazi Ahmed Mor Chowrangi",
            nameUrdu = "قاضی احمد موڑ چورنگی",
            category = "Highway",
            address = "Sakrand Bypass Link",
            lat = 26.2620,
            lng = 68.3900
        ),
        LocationItem(
            id = "bilawal_stadium",
            name = "Bilawal Sports Stadium",
            nameUrdu = "بلاول اسپورٹس اسٹیڈیم",
            category = "Sports",
            address = "Stadium Road, Nawabshah",
            lat = 26.2400,
            lng = 68.4200
        ),
        LocationItem(
            id = "monotechnic",
            name = "Mono Technic Institute",
            nameUrdu = "مونو ٹیکنک انسٹیٹیوٹ",
            category = "Education",
            address = "Sanghar Road, Nawabshah",
            lat = 26.2560,
            lng = 68.4230
        ),
        LocationItem(
            id = "harrison_branch",
            name = "Harrison Branch Canal Bridge",
            nameUrdu = "ہیریسن برانچ کینال پل",
            category = "Canal Link",
            address = "Old Nawabshah Bridge",
            lat = 26.2460,
            lng = 68.4000
        ),
        LocationItem(
            id = "liaquat_market",
            name = "Liaquat Market Bazaar",
            nameUrdu = "لیاقت مارکیٹ بازار",
            category = "Market",
            address = "Shahi Bazaar Road",
            lat = 26.2480,
            lng = 68.4150
        )
    )

    // Calculate approx distance in kilometers using Haversine formula
    fun calculateDistanceKm(loc1: LocationItem, loc2: LocationItem): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(loc2.lat - loc1.lat)
        val dLng = Math.toRadians(loc2.lng - loc1.lng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(loc1.lat)) * cos(Math.toRadians(loc2.lat)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val rawDist = earthRadius * c
        // Road factor in urban environment approx 1.3
        val distance = if (rawDist < 0.5) 1.2 else rawDist * 1.32
        return (Math.round(distance * 10.0) / 10.0)
    }

    // Baseline recommended fare calculation
    fun estimateFare(category: VehicleCategory, distanceKm: Double): Int {
        val calculated = category.baseFarePkr + (category.perKmRatePkr * distanceKm).toInt()
        // Round to nearest 10 PKR
        return (calculated / 10) * 10
    }

    // Pre-registered local Nawabshah Captains for bidding
    fun generateMockDriverBids(category: VehicleCategory, baselineFare: Int): List<DriverOffer> {
        return when (category.id) {
            "moto" -> listOf(
                DriverOffer(
                    id = "bid_1",
                    driverId = "drv_rashid",
                    driverName = "Rashid Brohi",
                    phone = "0301-9821456",
                    rating = 4.9f,
                    totalTrips = 840,
                    vehicleModel = "Honda CD 70",
                    vehicleColor = "Red",
                    plateNumber = "SBN-9421",
                    vehicleType = "Moto",
                    proposedFare = baselineFare,
                    etaMinutes = 3,
                    distanceAwayKm = 0.8,
                    currentLat = 26.2490,
                    currentLng = 26.2490
                ),
                DriverOffer(
                    id = "bid_2",
                    driverId = "drv_tariq",
                    driverName = "Tariq Baloch",
                    phone = "0312-4431980",
                    rating = 4.8f,
                    totalTrips = 530,
                    vehicleModel = "United 70cc",
                    vehicleColor = "Black",
                    plateNumber = "SBN-1144",
                    vehicleType = "Moto",
                    proposedFare = (baselineFare - 10).coerceAtLeast(60),
                    etaMinutes = 5,
                    distanceAwayKm = 1.4,
                    currentLat = 26.2440,
                    currentLng = 68.4110
                ),
                DriverOffer(
                    id = "bid_3",
                    driverId = "drv_ghulam",
                    driverName = "Ghulam Murtaza",
                    phone = "0345-7782109",
                    rating = 5.0f,
                    totalTrips = 1200,
                    vehicleModel = "Yamaha YBR 125",
                    vehicleColor = "Blue",
                    plateNumber = "SBN-6850",
                    vehicleType = "Moto",
                    proposedFare = baselineFare + 20,
                    etaMinutes = 2,
                    distanceAwayKm = 0.5,
                    currentLat = 26.2505,
                    currentLng = 68.4060
                )
            )
            "rickshaw" -> listOf(
                DriverOffer(
                    id = "bid_r1",
                    driverId = "drv_allah_ditta",
                    driverName = "Allah Ditta",
                    phone = "0300-3321980",
                    rating = 4.85f,
                    totalTrips = 950,
                    vehicleModel = "Sazgar 4-Stroke Auto",
                    vehicleColor = "Green / Yellow",
                    plateNumber = "NWS-AUTO-290",
                    vehicleType = "Auto Rickshaw",
                    proposedFare = baselineFare,
                    etaMinutes = 4,
                    distanceAwayKm = 1.1,
                    currentLat = 26.2510,
                    currentLng = 68.4100
                ),
                DriverOffer(
                    id = "bid_r2",
                    driverId = "drv_mushtaq",
                    driverName = "Ustad Mushtaq",
                    phone = "0333-8891234",
                    rating = 4.9f,
                    totalTrips = 1430,
                    vehicleModel = "Tezraftaar Qingqi 6-Seater",
                    vehicleColor = "Red / White",
                    plateNumber = "NWS-QNG-511",
                    vehicleType = "Qingqi Rickshaw",
                    proposedFare = (baselineFare - 20).coerceAtLeast(100),
                    etaMinutes = 6,
                    distanceAwayKm = 1.8,
                    currentLat = 26.2460,
                    currentLng = 68.4020
                )
            )
            "mini" -> listOf(
                DriverOffer(
                    id = "bid_m1",
                    driverId = "drv_waqas",
                    driverName = "Captain Waqas Jamali",
                    phone = "0303-6677881",
                    rating = 4.92f,
                    totalTrips = 610,
                    vehicleModel = "Suzuki Alto 660cc",
                    vehicleColor = "White",
                    plateNumber = "NWS-AL-448",
                    vehicleType = "Mini Car",
                    proposedFare = baselineFare,
                    etaMinutes = 4,
                    distanceAwayKm = 1.2,
                    currentLat = 26.2480,
                    currentLng = 68.4050
                ),
                DriverOffer(
                    id = "bid_m2",
                    driverId = "drv_farhan",
                    driverName = "Farhan Sindhi",
                    phone = "0315-9922114",
                    rating = 4.75f,
                    totalTrips = 380,
                    vehicleModel = "Suzuki Mehran VXR",
                    vehicleColor = "Silver",
                    plateNumber = "NWS-MH-109",
                    vehicleType = "Mini Car",
                    proposedFare = baselineFare - 30,
                    etaMinutes = 7,
                    distanceAwayKm = 2.0,
                    currentLat = 26.2430,
                    currentLng = 68.4150
                )
            )
            else -> listOf(
                DriverOffer(
                    id = "bid_s1",
                    driverId = "drv_imtiaz",
                    driverName = "Captain Imtiaz Ali Shah",
                    phone = "0300-8877112",
                    rating = 4.96f,
                    totalTrips = 1140,
                    vehicleModel = "Toyota Corolla GLi AC",
                    vehicleColor = "Black",
                    plateNumber = "SBN-VIP-007",
                    vehicleType = "Sedan AC",
                    proposedFare = baselineFare,
                    etaMinutes = 3,
                    distanceAwayKm = 0.9,
                    currentLat = 26.2520,
                    currentLng = 68.4070
                ),
                DriverOffer(
                    id = "bid_s2",
                    driverId = "drv_zeeshan",
                    driverName = "Zeeshan Ahmed",
                    phone = "0321-4455667",
                    rating = 4.88f,
                    totalTrips = 720,
                    vehicleModel = "Toyota Yaris ATIV",
                    vehicleColor = "Super White",
                    plateNumber = "NWS-YR-923",
                    vehicleType = "Sedan AC",
                    proposedFare = baselineFare + 50,
                    etaMinutes = 5,
                    distanceAwayKm = 1.5,
                    currentLat = 26.2460,
                    currentLng = 68.4120
                )
            )
        }
    }
}
