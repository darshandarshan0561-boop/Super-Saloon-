package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SaloonViewModel
import com.example.ui.components.MovingTickerBanner
import com.example.ui.components.StatusBadge
import com.example.ui.screens.LiveStatusScreen
import com.example.ui.screens.LocationScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.screens.VisitorLogScreen
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.OpenShopTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: SaloonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenShopTheme {
                OpenShopMainApp(viewModel = viewModel)
            }
        }
    }
}

enum class SaloonTab(val title: String, val icon: ImageVector) {
    LIVE_STATUS("Live Shop", Icons.Default.Storefront),
    VISITORS("Visitor Log", Icons.Default.People),
    SERVICES("Services", Icons.Default.ContentCut),
    LOCATION("Balale Map", Icons.Default.LocationOn)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenShopMainApp(viewModel: SaloonViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val currentStatus by viewModel.latestShopStatus.collectAsStateWithLifecycle()
    val allStatusLogs by viewModel.allShopStatusLogs.collectAsStateWithLifecycle()
    val latestAttendance by viewModel.latestOwnerAttendance.collectAsStateWithLifecycle()
    val attendanceLogs by viewModel.allOwnerAttendance.collectAsStateWithLifecycle()
    val filteredVisitors by viewModel.filteredVisitors.collectAsStateWithLifecycle()
    val activeVisitors by viewModel.activeVisitors.collectAsStateWithLifecycle()
    val services by viewModel.allServices.collectAsStateWithLifecycle()
    val visitorSearchQuery by viewModel.visitorSearchQuery.collectAsStateWithLifecycle()
    val visitorStatusFilter by viewModel.visitorStatusFilter.collectAsStateWithLifecycle()
    val currentTimeMillis by viewModel.currentTimeMillis.collectAsStateWithLifecycle()

    val currentStatusText = currentStatus?.status ?: "OPEN"
    val activeQueueSize = activeVisitors.size

    val announcementText = when (currentStatusText) {
        "OPEN" -> "Shop is OPEN in Balale (571219)! $activeQueueSize visitor(s) currently in queue."
        "CLOSED" -> "Shop is CLOSED now. Owner has left for the day. Reopening tomorrow morning."
        "BREAK" -> "Owner is currently ON BREAK. Will resume services shortly!"
        "LEFT" -> "Owner temporarily stepped out. Please leave your name in Visitor Log."
        else -> "Welcome to Open Shop Saloon in Balale!"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "OPEN SHOP",
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GoldPrimary.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("SALOON", color = GoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "📍 571219 Balale",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        StatusBadge(status = currentStatusText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = GoldPrimary,
                modifier = Modifier
                    .border(1.dp, SurfaceVariantDark)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                SaloonTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = ObsidianDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Moving Announcement Ticker
            MovingTickerBanner(
                text = announcementText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Dynamic Animated Tab Switching
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition",
                modifier = Modifier.weight(1f)
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> LiveStatusScreen(
                        currentStatus = currentStatus,
                        latestAttendance = latestAttendance,
                        attendanceLogs = attendanceLogs,
                        statusLogs = allStatusLogs,
                        currentTimeMillis = currentTimeMillis,
                        onUpdateStatus = { status, note -> viewModel.updateShopStatus(status, note) },
                        onOwnerPunch = { action, note -> viewModel.recordOwnerPunch(action, note) }
                    )
                    1 -> VisitorLogScreen(
                        visitors = filteredVisitors,
                        activeVisitors = activeVisitors,
                        services = services,
                        searchQuery = visitorSearchQuery,
                        statusFilter = visitorStatusFilter,
                        onSearchQueryChange = { query -> viewModel.setVisitorSearchQuery(query) },
                        onStatusFilterChange = { filter -> viewModel.setVisitorStatusFilter(filter) },
                        onAddVisitor = { name, phone, service -> viewModel.addVisitor(name, phone, service) },
                        onUpdateVisitorStatus = { id, status -> viewModel.updateVisitorStatus(id, status) },
                        onDeleteVisitor = { id -> viewModel.deleteVisitor(id) }
                    )
                    2 -> ServicesScreen(
                        services = services,
                        onBookService = { serviceName, name, phone ->
                            viewModel.addVisitor(name, phone, serviceName)
                        }
                    )
                    3 -> LocationScreen()
                }
            }
        }
    }
}
