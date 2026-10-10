package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.model.ActiveRide
import com.example.model.ChatMessage
import com.example.model.UserRole
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsNavyDark
import com.example.ui.theme.NwsYellow

@Composable
fun InRideChatSheet(
    activeRide: ActiveRide,
    chatMessages: List<ChatMessage>,
    currentUserRole: UserRole,
    isUrdu: Boolean,
    onSendMessage: (String) -> Unit,
    onCloseChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Pakistani Cab Quick phrases
    val quickPhrases = remember {
        listOf(
            "Main bahir khara hoon 📍",
            "Location par pohonch raha hoon ⏱️",
            "Kahan par hain aap? 🤔",
            "Main gate ke samne hon 🚪",
            "Khulay paise / change hain? 💵",
            "Traffic jam hai 2 min dein 🚗"
        )
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .testTag("sheet_in_ride_chat"),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NwsYellow,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = NwsNavyDark
                            )
                        }
                    }

                    Column {
                        Text(
                            text = activeRide.assignedDriver?.driverName ?: "Captain NWS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(NwsGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${activeRide.assignedDriver?.vehicleModel ?: "Cab"} • Live Chat",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onCloseChat,
                    modifier = Modifier.testTag("btn_close_chat")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Chat")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Message Bubble History
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    val isMyMsg = msg.senderRole == currentUserRole
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMyMsg) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMyMsg) 16.dp else 4.dp,
                                bottomEnd = if (isMyMsg) 4.dp else 16.dp
                            ),
                            color = if (isMyMsg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shadowElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .widthIn(max = 260.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    color = if (isMyMsg) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 10.sp,
                                    color = if (isMyMsg) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pakistani Quick Replies
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickPhrases) { phrase ->
                    SuggestionChip(
                        onClick = { onSendMessage(phrase) },
                        label = { Text(text = phrase, fontSize = 11.sp) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("chip_quick_phrase")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (isUrdu) "پیغام لکھیں..." else "Type message to Captain...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_chat_message"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_send_chat"),
                    shape = CircleShape,
                    containerColor = NwsYellow,
                    contentColor = NwsNavyDark
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
