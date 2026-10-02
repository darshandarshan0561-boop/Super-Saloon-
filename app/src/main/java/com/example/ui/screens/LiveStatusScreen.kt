package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OwnerAttendanceEntity
import com.example.data.ShopStatusEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatDate
import com.example.ui.components.formatElapsedDuration
import com.example.ui.components.formatTime
import com.example.ui.theme.BreakAmber
import com.example.ui.theme.ClosedRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.OpenGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiveStatusScreen(
    currentStatus: ShopStatusEntity?,
    latestAttendance: OwnerAttendanceEntity?,
    attendanceLogs: List<OwnerAttendanceEntity>,
    statusLogs: List<ShopStatusEntity>,
    currentTimeMillis: Long,
    onUpdateStatus: (status: String, note: String) -> Unit,
    onOwnerPunch: (action: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNoteInput by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    var selectedTargetStatus by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Live Status Card
        item {
            HeroShopStatusCard(
                currentStatus = currentStatus,
                latestAttendance = latestAttendance,
                currentTimeMillis = currentTimeMillis,
                onSelectQuickStatus = { targetStatus ->
                    selectedTargetStatus = targetStatus
                    showNoteInput = true
                }
            )
        }

        // Quick Status Change Dialog / Note Input Block
        if (showNoteInput) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Set Status to: ${selectedTargetStatus ?: "New Status"}",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("Add status note or leave reason (e.g., 'Back in 20 mins')", color = TextMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("status_note_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = TextMuted,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    showNoteInput = false
                                    noteText = ""
                                    selectedTargetStatus = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceHighlight)
                            ) {
                                Text("Cancel", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    selectedTargetStatus?.let { status ->
                                        onUpdateStatus(status, noteText)
                                    }
                                    showNoteInput = false
                                    noteText = ""
                                    selectedTargetStatus = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                modifier = Modifier.testTag("submit_status_button")
                            ) {
                                Text("Confirm Status", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Owner Attendance Punch Control Card
        item {
            OwnerAttendanceControlCard(
                latestAttendance = latestAttendance,
                currentTimeMillis = currentTimeMillis,
                onOwnerPunch = onOwnerPunch
            )
        }

        // Attendance & Punch Log History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Owner Attendance & Leave Log",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        if (attendanceLogs.isEmpty()) {
            item {
                Text(
                    text = "No attendance logs recorded yet.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(attendanceLogs) { attendance ->
                AttendanceLogItemCard(attendance = attendance)
            }
        }
    }
}

@Composable
fun HeroShopStatusCard(
    currentStatus: ShopStatusEntity?,
    latestAttendance: OwnerAttendanceEntity?,
    currentTimeMillis: Long,
    onSelectQuickStatus: (String) -> Unit
) {
    val status = currentStatus?.status ?: "OPEN"
    val startTime = currentStatus?.timestamp ?: System.currentTimeMillis()
    val note = currentStatus?.note ?: ""

    val (cardBorder, bgGradient) = when (status) {
        "OPEN" -> OpenGreen to Brush.verticalGradient(listOf(SurfaceDark, SurfaceVariantDark))
        "CLOSED" -> ClosedRed to Brush.verticalGradient(listOf(SurfaceDark, SurfaceVariantDark))
        "BREAK" -> BreakAmber to Brush.verticalGradient(listOf(SurfaceDark, SurfaceVariantDark))
        "LEFT" -> ClosedRed to Brush.verticalGradient(listOf(SurfaceDark, SurfaceVariantDark))
        else -> GoldPrimary to Brush.verticalGradient(listOf(SurfaceDark, SurfaceVariantDark))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, cardBorder.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .background(bgGradient)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OPEN SHOP SALOON",
                        color = GoldLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Balale, Pincode 571219",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                StatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timer display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianDark.copy(alpha = 0.8f))
                    .border(1.dp, SurfaceHighlight, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ACTIVE DURATION",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = formatElapsedDuration(startTime, currentTimeMillis),
                            color = GoldPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "LAST UPDATED",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = formatTime(startTime),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (note.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "💬 Status Note: \"$note\"",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Quick Status Update:",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatusChip(
                    label = "OPEN",
                    color = OpenGreen,
                    isSelected = status == "OPEN",
                    onClick = { onSelectQuickStatus("OPEN") },
                    modifier = Modifier.weight(1f).testTag("quick_status_open")
                )
                QuickStatusChip(
                    label = "BREAK",
                    color = BreakAmber,
                    isSelected = status == "BREAK",
                    onClick = { onSelectQuickStatus("BREAK") },
                    modifier = Modifier.weight(1f).testTag("quick_status_break")
                )
                QuickStatusChip(
                    label = "LEFT",
                    color = ClosedRed,
                    isSelected = status == "LEFT",
                    onClick = { onSelectQuickStatus("LEFT") },
                    modifier = Modifier.weight(1f).testTag("quick_status_left")
                )
                QuickStatusChip(
                    label = "CLOSED",
                    color = ClosedRed,
                    isSelected = status == "CLOSED",
                    onClick = { onSelectQuickStatus("CLOSED") },
                    modifier = Modifier.weight(1f).testTag("quick_status_closed")
                )
            }
        }
    }
}

@Composable
fun QuickStatusChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) color.copy(alpha = 0.3f) else SurfaceVariantDark)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) color else SurfaceHighlight,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) color else TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun OwnerAttendanceControlCard(
    latestAttendance: OwnerAttendanceEntity?,
    currentTimeMillis: Long,
    onOwnerPunch: (action: String, note: String) -> Unit
) {
    var punchNote by remember { mutableStateOf("") }
    val lastAction = latestAttendance?.actionType ?: "PUNCH_OUT"
    val isPresent = lastAction == "PUNCH_IN" || lastAction == "RETURNED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isPresent) OpenGreen else ClosedRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPresent) "Owner Present in Shop" else "Owner Away / Punched Out",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Text(
                    text = latestAttendance?.timestamp?.let { formatTime(it) } ?: "--:--",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = punchNote,
                onValueChange = { punchNote = it },
                placeholder = { Text("Attendance note (e.g. 'Leaving for lunch break', 'Punched in for morning shift')", color = TextMuted) },
                modifier = Modifier.fillMaxWidth().testTag("punch_note_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = SurfaceHighlight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPresent) {
                    Button(
                        onClick = {
                            onOwnerPunch("TEMPORARY_LEAVE", punchNote.ifBlank { "Owner stepped out" })
                            punchNote = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BreakAmber),
                        modifier = Modifier.weight(1f).testTag("button_step_out")
                    ) {
                        Icon(Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Step Out", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            onOwnerPunch("PUNCH_OUT", punchNote.ifBlank { "Owner punched out / Closed" })
                            punchNote = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClosedRed),
                        modifier = Modifier.weight(1f).testTag("button_punch_out")
                    ) {
                        Icon(Icons.Default.LockClock, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Punch Out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            onOwnerPunch("PUNCH_IN", punchNote.ifBlank { "Owner checked in at Balale Shop" })
                            punchNote = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OpenGreen),
                        modifier = Modifier.fillMaxWidth().testTag("button_punch_in")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Punch In / I Am Here at Shop", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceLogItemCard(attendance: OwnerAttendanceEntity) {
    val (actionColor, actionLabel) = when (attendance.actionType) {
        "PUNCH_IN", "RETURNED" -> OpenGreen to "PUNCHED IN"
        "PUNCH_OUT" -> ClosedRed to "PUNCHED OUT"
        "TEMPORARY_LEAVE" -> BreakAmber to "STEPPED OUT"
        else -> GoldPrimary to attendance.actionType
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(actionColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = actionLabel,
                        color = actionColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (attendance.note.isNotBlank()) {
                        Text(
                            text = attendance.note,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatTime(attendance.timestamp),
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Text(
                    text = formatDate(attendance.timestamp),
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
