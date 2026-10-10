package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NawabshahData
import com.example.model.ActiveRide
import com.example.model.LocationItem
import com.example.model.RideStatus
import com.example.ui.theme.*
import kotlin.math.*

enum class MapThemeMode {
    LIGHT_VECTOR,
    DARK_NIGHT,
    SATELLITE_HYBRID
}

@Composable
fun NawabshahMapView(
    activeRide: ActiveRide?,
    pickupLocation: LocationItem,
    dropoffLocation: LocationItem,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    highContrast: Boolean = false
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var mapMode by remember { mutableStateOf(if (isDark || highContrast) MapThemeMode.DARK_NIGHT else MapThemeMode.LIGHT_VECTOR) }
    var showTraffic by remember { mutableStateOf(true) }

    // Pulsing animation for pickup & driver marker
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    // Bounding box for Nawabshah area: Lat ~26.22 to 26.27, Lng ~68.38 to 68.43
    val minLat = 26.220
    val maxLat = 26.275
    val minLng = 68.380
    val maxLng = 68.435

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.7f, 3.0f)
                    panOffset += pan
                }
            }
            .testTag("nawabshah_map_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            fun toScreenCoord(lat: Double, lng: Double): Offset {
                val normX = ((lng - minLng) / (maxLng - minLng)).toFloat()
                // Invert Y since latitude increases northward
                val normY = (1f - ((lat - minLat) / (maxLat - minLat))).toFloat()

                val cx = canvasW / 2f
                val cy = canvasH / 2f

                val sx = (normX * canvasW - cx) * zoomScale + cx + panOffset.x
                val sy = (normY * canvasH - cy) * zoomScale + cy + panOffset.y
                return Offset(sx, sy)
            }

            // 1. Draw Map Background based on MapThemeMode
            val bgColor = when {
                highContrast -> Color(0xFF050505)
                mapMode == MapThemeMode.DARK_NIGHT -> Color(0xFF0F172A)
                mapMode == MapThemeMode.SATELLITE_HYBRID -> Color(0xFF1E293B)
                else -> Color(0xFFF1F5F9)
            }
            drawRect(color = bgColor)

            // 2. Draw Harrison Branch Canal (Nawabshah River / Canal artery)
            val canalPath = Path().apply {
                val p1 = toScreenCoord(26.225, 68.390)
                val p2 = toScreenCoord(26.240, 68.398)
                val p3 = toScreenCoord(26.255, 68.406)
                val p4 = toScreenCoord(26.272, 68.415)
                moveTo(p1.x, p1.y)
                cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)
            }
            drawPath(
                path = canalPath,
                color = Color(0xFF0284C7).copy(alpha = 0.45f),
                style = Stroke(width = 16f * zoomScale, cap = StrokeCap.Round)
            )

            // 3. Draw Road Network of Nawabshah
            val roadColor = if (mapMode == MapThemeMode.DARK_NIGHT || highContrast) Color(0xFF334155) else Color(0xFFCBD5E1)
            val mainRoadColor = if (mapMode == MapThemeMode.DARK_NIGHT || highContrast) Color(0xFF475569) else Color(0xFFE2E8F0)

            // Main Arteries (Sakrand Road, Court Road, VIP Road, Station Road, Highway Link)
            drawNawabshahRoadGrid(::toScreenCoord, roadColor, mainRoadColor, zoomScale, showTraffic)

            // 4. Draw Active Route Line if ride exists or between selected pickup and dropoff
            val pickupScreen = toScreenCoord(pickupLocation.lat, pickupLocation.lng)
            val dropoffScreen = toScreenCoord(dropoffLocation.lat, dropoffLocation.lng)

            val routePath = Path().apply {
                moveTo(pickupScreen.x, pickupScreen.y)
                // Curve intermediate waypoint around Society Chowk for organic city route
                val midLat = (pickupLocation.lat + dropoffLocation.lat) / 2.0 + 0.002
                val midLng = (pickupLocation.lng + dropoffLocation.lng) / 2.0 - 0.001
                val midScreen = toScreenCoord(midLat, midLng)
                quadraticTo(midScreen.x, midScreen.y, dropoffScreen.x, dropoffScreen.y)
            }

            // Glow under route
            drawPath(
                path = routePath,
                color = NwsCyan.copy(alpha = 0.3f),
                style = Stroke(width = 14f * zoomScale, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Solid route line
            drawPath(
                path = routePath,
                color = if (highContrast) HighContrastYellow else NwsCyan,
                style = Stroke(
                    width = 6f * zoomScale,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 5. Draw Nawabshah Key Landmark Dots
            NawabshahData.locations.forEach { loc ->
                val pt = toScreenCoord(loc.lat, loc.lng)
                if (loc.id != pickupLocation.id && loc.id != dropoffLocation.id) {
                    drawCircle(
                        color = if (mapMode == MapThemeMode.DARK_NIGHT || highContrast) Color(0xFF64748B) else Color(0xFF94A3B8),
                        radius = 4f * zoomScale,
                        center = pt
                    )
                }
            }

            // 6. Draw Pickup Marker (Emerald Green Pulse)
            drawCircle(
                color = NwsGreen.copy(alpha = 0.35f),
                radius = pulseRadius * zoomScale,
                center = pickupScreen
            )
            drawCircle(
                color = NwsGreen,
                radius = 9f * zoomScale,
                center = pickupScreen
            )
            drawCircle(
                color = Color.White,
                radius = 4f * zoomScale,
                center = pickupScreen
            )

            // 7. Draw Dropoff Marker (Crimson / Orange Pin)
            drawCircle(
                color = NwsRedSos.copy(alpha = 0.25f),
                radius = 14f * zoomScale,
                center = dropoffScreen
            )
            drawCircle(
                color = NwsRedSos,
                radius = 9f * zoomScale,
                center = dropoffScreen
            )
            drawCircle(
                color = Color.White,
                radius = 4f * zoomScale,
                center = dropoffScreen
            )

            // 8. Draw Moving Driver Marker if active ride in progress
            if (activeRide != null && activeRide.status != RideStatus.IDLE && activeRide.status != RideStatus.REQUESTING_BIDS) {
                val driverPt = if (activeRide.driverLat != 0.0) {
                    toScreenCoord(activeRide.driverLat, activeRide.driverLng)
                } else {
                    // Interpolated point along route
                    val p = activeRide.currentProgress.coerceIn(0f, 1f)
                    Offset(
                        pickupScreen.x + (dropoffScreen.x - pickupScreen.x) * p,
                        pickupScreen.y + (dropoffScreen.y - pickupScreen.y) * p
                    )
                }

                // Driver vehicle beacon
                drawCircle(
                    color = NwsYellow.copy(alpha = 0.4f),
                    radius = (pulseRadius + 4f) * zoomScale,
                    center = driverPt
                )
                drawCircle(
                    color = NwsYellow,
                    radius = 12f * zoomScale,
                    center = driverPt
                )
                drawCircle(
                    color = NwsNavyDark,
                    radius = 9f * zoomScale,
                    center = driverPt
                )
                // Inner bright gold indicator
                drawCircle(
                    color = Color.White,
                    radius = 4f * zoomScale,
                    center = driverPt
                )
            }
        }

        // Live Telemetry HUD Bar (Top of Map)
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .testTag("map_telemetry_hud"),
            shape = RoundedCornerShape(24.dp),
            color = if (highContrast) Color.Black else NwsNavyDark.copy(alpha = 0.92f),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "GPS status",
                        tint = NwsGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GPS: Nawabshah Live",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(14.dp)
                        .width(1.dp),
                    color = Color.Gray
                )

                if (activeRide != null && activeRide.status == RideStatus.IN_PROGRESS) {
                    Text(
                        text = "Speed: 38 km/h",
                        fontSize = 11.sp,
                        color = NwsYellow,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Divider(
                        modifier = Modifier
                            .height(14.dp)
                            .width(1.dp),
                        color = Color.Gray
                    )
                }

                Text(
                    text = "Dist: ${activeRide?.distanceKm ?: NawabshahData.calculateDistanceKm(pickupLocation, dropoffLocation)} km",
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }

        // Floating Map Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { zoomScale = (zoomScale * 1.25f).coerceAtMost(3.0f) },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("btn_zoom_in"),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = { zoomScale = (zoomScale * 0.8f).coerceAtLeast(0.7f) },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("btn_zoom_out"),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = {
                    panOffset = Offset.Zero
                    zoomScale = 1.0f
                },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("btn_center_map"),
                shape = CircleShape,
                containerColor = NwsYellow,
                contentColor = NwsNavyDark
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center on Nawabshah", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = {
                    mapMode = when (mapMode) {
                        MapThemeMode.LIGHT_VECTOR -> MapThemeMode.DARK_NIGHT
                        MapThemeMode.DARK_NIGHT -> MapThemeMode.SATELLITE_HYBRID
                        MapThemeMode.SATELLITE_HYBRID -> MapThemeMode.LIGHT_VECTOR
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("btn_map_layers"),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.Layers, contentDescription = "Map Style Layer", modifier = Modifier.size(20.dp))
            }

            FloatingActionButton(
                onClick = { showTraffic = !showTraffic },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("btn_traffic_toggle"),
                shape = CircleShape,
                containerColor = if (showTraffic) NwsGreen else MaterialTheme.colorScheme.surface,
                contentColor = if (showTraffic) Color.White else MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.Traffic, contentDescription = "Toggle Traffic", modifier = Modifier.size(20.dp))
            }
        }
    }
}

private fun DrawScope.drawNawabshahRoadGrid(
    toScreenCoord: (Double, Double) -> Offset,
    roadColor: Color,
    mainRoadColor: Color,
    scale: Float,
    showTraffic: Boolean
) {
    // 1. Sakrand Road to QUEST (diagonal southwest to northeast)
    drawLine(
        color = mainRoadColor,
        start = toScreenCoord(26.222, 68.385),
        end = toScreenCoord(26.252, 68.410),
        strokeWidth = 7f * scale
    )

    // 2. VIP / Court Road (connecting Society Chowk, Civil Hospital, PUMHS)
    drawLine(
        color = mainRoadColor,
        start = toScreenCoord(26.242, 68.412),
        end = toScreenCoord(26.265, 68.405),
        strokeWidth = 8f * scale
    )

    // 3. Station Road (East-West artery through Nawabshah City)
    drawLine(
        color = mainRoadColor,
        start = toScreenCoord(26.248, 68.395),
        end = toScreenCoord(26.244, 68.428),
        strokeWidth = 9f * scale
    )

    // 4. National Highway Bypass Link
    drawLine(
        color = roadColor,
        start = toScreenCoord(26.262, 68.390),
        end = toScreenCoord(26.273, 68.430),
        strokeWidth = 6f * scale
    )

    // 5. Sanghar Road Link to Mono Technic
    drawLine(
        color = roadColor,
        start = toScreenCoord(26.250, 68.415),
        end = toScreenCoord(26.260, 68.432),
        strokeWidth = 6f * scale
    )

    // 6. Taj Colony Residential Grid
    for (i in 0..3) {
        val latOffset = i * 0.003
        drawLine(
            color = roadColor.copy(alpha = 0.6f),
            start = toScreenCoord(26.253 + latOffset, 68.405),
            end = toScreenCoord(26.253 + latOffset, 68.418),
            strokeWidth = 3.5f * scale
        )
    }

    // Traffic Flow Highlight (Green / Amber live conditions along Society Chowk & Station Road)
    if (showTraffic) {
        drawLine(
            color = Color(0xFF10B981).copy(alpha = 0.7f),
            start = toScreenCoord(26.245, 68.414),
            end = toScreenCoord(26.251, 68.408),
            strokeWidth = 3f * scale
        )
        drawLine(
            color = Color(0xFFF59E0B).copy(alpha = 0.8f),
            start = toScreenCoord(26.249, 68.407),
            end = toScreenCoord(26.253, 68.406),
            strokeWidth = 3.5f * scale
        )
    }
}
