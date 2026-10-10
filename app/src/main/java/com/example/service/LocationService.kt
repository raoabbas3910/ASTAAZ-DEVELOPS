package com.example.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.NawabshahData
import com.example.model.LocationItem
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Service to manage real-time GPS location tracking for riders and drivers in TRAVEL NWS.
 * Utilizes Google Play Services FusedLocationProviderClient.
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Checks if either FINE or COARSE location permission is granted.
     */
    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    /**
     * Retrieves the last known device location.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) return null
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Emits continuous real-time GPS location updates as a Kotlin Flow.
     * High accuracy configured for active driver navigation and rider pickup tracking.
     */
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(
        intervalMillis: Long = 4000L,
        minUpdateDistanceMeters: Float = 2.0f
    ): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        ).apply {
            setMinUpdateIntervalMillis(intervalMillis / 2)
            setMinUpdateDistanceMeters(minUpdateDistanceMeters)
            setWaitForAccurateLocation(false)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(loc)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    /**
     * Matches live GPS coordinate with the nearest registered Nawabshah landmark.
     */
    fun findNearestNawabshahLandmark(userLat: Double, userLng: Double): LocationItem {
        var closest = NawabshahData.locations.first()
        var minDistance = Float.MAX_VALUE

        for (loc in NawabshahData.locations) {
            val dist = calculateDistanceBetween(userLat, userLng, loc.lat, loc.lng)
            if (dist < minDistance) {
                minDistance = dist
                closest = loc
            }
        }
        return closest
    }

    /**
     * Calculates distance in meters between two GPS coordinates.
     */
    fun calculateDistanceBetween(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }
}
