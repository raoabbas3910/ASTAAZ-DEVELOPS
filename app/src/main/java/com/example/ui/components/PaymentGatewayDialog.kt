package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.ActiveRide
import com.example.model.PaymentMethod
import com.example.ui.theme.NwsGreen
import com.example.ui.theme.NwsYellow

@Composable
fun PaymentGatewayDialog(
    activeRide: ActiveRide,
    walletBalance: Int,
    isUrdu: Boolean,
    onConfirmPayment: (PaymentMethod) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(activeRide.paymentMethod) }
    var mobileAccountInput by remember { mutableStateOf("03001234567") }
    var isProcessing by remember { mutableStateOf(false) }

    val options = listOf(
        PaymentOption(PaymentMethod.CASH, "Cash on Arrival", "نقد کیپٹن کو دیں", Icons.Default.Payments, "Pay directly to driver"),
        PaymentOption(PaymentMethod.JAZZCASH, "JazzCash Account", "جاز کیش اکاؤنٹ", Icons.Default.PhoneAndroid, "Instant mobile PIN debit"),
        PaymentOption(PaymentMethod.EASYPAISA, "Easypaisa Account", "ایزی پیسہ اکاؤنٹ", Icons.Default.AccountBalanceWallet, "Direct wallet deduction"),
        PaymentOption(PaymentMethod.NWS_WALLET, "NWS In-App Wallet", "ٹریول نواب شاہ والٹ", Icons.Default.Savings, "Balance: Rs. $walletBalance"),
        PaymentOption(PaymentMethod.CARD, "Visa / Mastercard", "ڈیبٹ یا کریڈٹ کارڈ", Icons.Default.CreditCard, "Secure 3D chip pay")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_payment_gateway"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = NwsGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "محفوظ ادائیگی (Secure Payment)" else "Secure Payment Gateway",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Receipt summary
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isUrdu) "کل واجب الادا رقم:" else "Total Fare Amount:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Rs. ${activeRide.agreedFare}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "Route: ${activeRide.pickup.name} -> ${activeRide.dropoff.name}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    text = if (isUrdu) "ادائیگی کا طریقہ منتخب کریں:" else "Select Payment Method:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                // Method Selector List
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    options.forEach { opt ->
                        val isSelected = selectedMethod == opt.method
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) NwsYellow else Color.Gray.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedMethod = opt.method }
                                .testTag("radio_payment_${opt.method.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = opt.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) NwsYellow else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isUrdu) opt.titleUrdu else opt.title,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = opt.subtitle,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedMethod = opt.method }
                                )
                            }
                        }
                    }
                }

                // Extra input if JazzCash / EasyPaisa
                if (selectedMethod == PaymentMethod.JAZZCASH || selectedMethod == PaymentMethod.EASYPAISA) {
                    OutlinedTextField(
                        value = mobileAccountInput,
                        onValueChange = { mobileAccountInput = it },
                        label = { Text("Account Number") },
                        placeholder = { Text("03XX-XXXXXXX") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_mobile_account")
                    )
                }

                if (isProcessing) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Verifying with gateway...", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isProcessing = true
                    onConfirmPayment(selectedMethod)
                },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = NwsGreen),
                modifier = Modifier.testTag("btn_complete_payment")
            ) {
                Text(
                    text = if (isUrdu) "ادائیگی تصدیق کریں (Rs. ${activeRide.agreedFare})" else "Confirm Payment (Rs. ${activeRide.agreedFare})",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_payment")
            ) {
                Text(text = if (isUrdu) "منسوخ" else "Cancel")
            }
        }
    )
}

private data class PaymentOption(
    val method: PaymentMethod,
    val title: String,
    val titleUrdu: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val subtitle: String
)
