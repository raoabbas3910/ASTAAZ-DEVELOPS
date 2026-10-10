package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NawabshahData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TRAVEL NWS", appName)
  }

  @Test
  fun `test Nawabshah distance and fare estimation`() {
    val quest = NawabshahData.locations[0] // QUEST
    val station = NawabshahData.locations[2] // Railway Station
    val distance = NawabshahData.calculateDistanceKm(quest, station)
    assertTrue("Distance between QUEST and Railway Station should be positive", distance > 1.0)

    val motoCategory = NawabshahData.vehicleCategories[0]
    val fare = NawabshahData.estimateFare(motoCategory, distance)
    assertTrue("Fare should be at least base fare", fare >= motoCategory.baseFarePkr)
  }

  @Test
  fun `test LocationService landmark matching and distance calculation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val locationService = com.example.service.LocationService(context)

    // Check landmark matching near QUEST (26.2420, 68.3980)
    val landmark = locationService.findNearestNawabshahLandmark(26.2421, 68.3982)
    assertEquals("QUEST University", landmark.name)

    // Check distance calculation
    val distMeters = locationService.calculateDistanceBetween(26.2420, 68.3980, 26.2490, 68.4120)
    assertTrue("Distance should be greater than 0", distMeters > 0f)
  }
}
