package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun LocationScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val shopPhone = "+919845057121"
    val shopAddress = "Open Shop Saloon, Main Road, Balale - 571219, Karnataka, India"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Interactive Vector Map Illustration Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(SurfaceVariantDark)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height

                            // Draw Map Grid / Roads
                            val mainRoadPath = Path().apply {
                                moveTo(0f, height * 0.6f)
                                cubicTo(width * 0.3f, height * 0.5f, width * 0.7f, height * 0.7f, width, height * 0.4f)
                            }
                            drawPath(
                                path = mainRoadPath,
                                color = GoldPrimary.copy(alpha = 0.6f),
                                style = Stroke(width = 16f)
                            )

                            val crossRoadPath = Path().apply {
                                moveTo(width * 0.5f, 0f)
                                lineTo(width * 0.5f, height)
                            }
                            drawPath(
                                path = crossRoadPath,
                                color = Color.Gray.copy(alpha = 0.4f),
                                style = Stroke(width = 10f)
                            )

                            // Draw Shop Pin at Center Intersection
                            val pinCenter = Offset(width * 0.5f, height * 0.55f)

                            // Outer pulse rings
                            drawCircle(
                                color = OpenGreen.copy(alpha = 0.25f),
                                radius = 32f,
                                center = pinCenter
                            )
                            drawCircle(
                                color = OpenGreen.copy(alpha = 0.5f),
                                radius = 20f,
                                center = pinCenter
                            )
                            drawCircle(
                                color = GoldPrimary,
                                radius = 10f,
                                center = pinCenter
                            )
                        }

                        // Map Pin Overlay Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GoldPrimary)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("OPEN SHOP SALOON", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            }
                        }

                        // Map Legend
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ObsidianDark.copy(alpha = 0.85f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("📍 Balale 571219 Map View", color = GoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("OPEN SHOP SALOON", color = GoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(shopAddress, color = TextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$shopPhone")
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OpenGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_call_saloon")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Shop", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=Balale+571219+Open+Shop+Saloon")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=Balale+571219"))
                                        context.startActivity(webIntent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_maps_directions")
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Directions", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Shop Operating Timings & Details
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceHighlight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Operating Hours", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TimingRow("Monday - Friday", "7:30 AM - 9:30 PM", isToday = true)
                    TimingRow("Saturday", "7:00 AM - 10:00 PM", isToday = false)
                    TimingRow("Sunday", "7:00 AM - 10:00 PM", isToday = false)
                }
            }
        }

        // Share & Website Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceHighlight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Balale Open Shop Website & Portal", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Share current shop live status & visitor attendance token updates with clients.", color = TextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check Open Shop Saloon Live Status & Queue in Balale (571219): Open Shop is active! Call $shopPhone")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Open Shop Saloon Link"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_share_saloon"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Shop Status & Location Link", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TimingRow(dayRange: String, timeRange: String, isToday: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isToday) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(OpenGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = dayRange,
                color = if (isToday) GoldLight else TextSecondary,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
        Text(
            text = timeRange,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}
