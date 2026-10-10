package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveRide
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsNavyDark
import com.example.ui.theme.NwsYellow

@Composable
fun RatingFeedbackDialog(
    activeRide: ActiveRide,
    isUrdu: Boolean,
    onSubmitRating: (Float, String) -> Unit,
    onDismiss: () -> Unit
) {
    var ratingStars by remember { mutableFloatStateOf(5f) }
    var reviewNote by remember { mutableStateOf("") }
    val selectedBadges = remember { mutableStateListOf<String>() }

    val badges = listOf(
        "Safe Driving 🛡️",
        "Polite Captain 🌟",
        "Clean Vehicle ✨",
        "Exact Change 💵",
        "On Time ⏱️",
        "Great Music 🎵"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_rating_feedback"),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = CircleShape,
                    color = NwsYellow,
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = activeRide.assignedDriver?.driverName?.firstOrNull()?.toString() ?: "C",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = NwsNavyDark
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isUrdu) "کیپٹن کو ریٹنگ دیں" else "Rate Your Trip",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${activeRide.assignedDriver?.driverName ?: "Captain"} (${activeRide.assignedDriver?.vehicleModel ?: "Cab"})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive 5 stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= ratingStars) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (i <= ratingStars) Color(0xFFFFB703) else Color.Gray,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { ratingStars = i.toFloat() }
                                .testTag("star_rate_$i")
                        )
                    }
                }

                Text(
                    text = if (isUrdu) "تعریفی بیجز منتخب کریں:" else "Select compliments for Captain:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Compliment chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(badges) { badge ->
                        val isSelected = selectedBadges.contains(badge)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedBadges.remove(badge)
                                else selectedBadges.add(badge)
                            },
                            label = { Text(text = badge, fontSize = 11.sp) },
                            modifier = Modifier.testTag("chip_badge_$badge")
                        )
                    }
                }

                // Text review
                OutlinedTextField(
                    value = reviewNote,
                    onValueChange = { reviewNote = it },
                    placeholder = {
                        Text(
                            text = if (isUrdu) "کوئی رائے یا کمنٹ لکھیں..." else "Leave a note for the captain...",
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_rating_review"),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fullFeedback = (selectedBadges + reviewNote.trim()).filter { it.isNotBlank() }.joinToString(", ")
                    onSubmitRating(ratingStars, fullFeedback)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NwsYellow),
                modifier = Modifier.testTag("btn_submit_rating")
            ) {
                Text(
                    text = if (isUrdu) "ریٹنگ جمع کروائیں" else "Submit Rating",
                    fontWeight = FontWeight.Bold,
                    color = NwsNavyDark
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isUrdu) "بعد میں" else "Skip")
            }
        }
    )
}
