package com.example.studentattandance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentattandance.ui.theme.*

/**
 * Sessions Screen
 * 
 * Displays user's class sessions with filtering options (Today, Upcoming, Past)
 * 
 *  ALL DATA IS DYNAMIC - Provided from backend API
 * 
 * @param userAvatarUrl Optional URL for user avatar (from API: user.avatarUrl)
 *                      If null, shows default user icon
 *                      Use image loading library (Coil/Glide) to load from URL
 * @param sessions List of sessions to display (from API: response.sessions)
 *                 Map API response to Session data class - see Session class documentation
 * @param onNotificationClick Callback when notification bell is clicked
 * @param onSessionClick Callback when a session card is clicked
 * @param onCheckInClick Callback when check-in button is clicked (opens QR scanner)
 * 
 * Example API Integration:
 * ```
 * val sessionsResponse = apiService.getSessions()
 * SessionsScreen(
 *     userAvatarUrl = sessionsResponse.user.avatarUrl,
 *     sessions = sessionsResponse.sessions.map { it.toSession() },
 *     onNotificationClick = { navController.navigate("notifications") },
 *     onSessionClick = { session -> navController.navigate("session/${session.id}") },
 *     onCheckInClick = { session -> openQRScanner(session.id) }
 * )
 * ```
 */
@Composable
fun SessionsScreen(
    userAvatarUrl: String? = null,
    sessions: List<Session> = sampleSessions(), //  DUMMY DATA - Replace with API call
    onNotificationClick: () -> Unit = {},
    onSessionClick: (Session) -> Unit = {},
    onCheckInClick: (Session) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(SessionTab.TODAY) }
    
    // Filter sessions based on selected tab
    val filteredSessions = sessions.filter { session ->
        when (selectedTab) {
            SessionTab.TODAY -> session.status == SessionStatus.LIVE_NOW || 
                               (session.status == SessionStatus.STARTS_SOON && session.isToday)
            SessionTab.UPCOMING -> session.status == SessionStatus.UPCOMING
            SessionTab.PAST -> session.status == SessionStatus.PAST
        }
    }
    
    Scaffold(
        containerColor = DeepNavy
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header with avatar and notification
            SessionsHeader(
                userAvatarUrl = userAvatarUrl,
                onNotificationClick = onNotificationClick
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = "My Sessions",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Tab filters
            SessionTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sessions list - SCROLLABLE
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Group sessions by date
                val groupedSessions = filteredSessions.groupBy { it.date }
                
                groupedSessions.forEach { (date, sessionsForDate) ->
                    item {
                        Text(
                            text = date,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(sessionsForDate) { session ->
                        SessionCard(
                            session = session,
                            onClick = { onSessionClick(session) },
                            onCheckInClick = { onCheckInClick(session) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionsHeader(
    userAvatarUrl: String?,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Avatar - DYNAMIC (from API)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CardNavy)
                .border(2.dp, AccentBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (userAvatarUrl != null) {
                // TODO: Load image from URL using Coil or Glide
                // AsyncImage(model = userAvatarUrl, contentDescription = "User Avatar")
                // For now, show default user icon
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(AccentBlue)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(AccentBlue)
                )
            }
        }
        
        // Notification Bell
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CardNavy)
                .clickable(onClick = onNotificationClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.notification_bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(TextPrimary)
            )
            
            // Notification badge
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5757))
            )
        }
    }
}

@Composable
fun SessionTabRow(
    selectedTab: SessionTab,
    onTabSelected: (SessionTab) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SessionTab.values().forEach { tab ->
            SessionTabButton(
                text = tab.title,
                isSelected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
fun SessionTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) AccentBlue else CardNavy
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else TextPrimary.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun SessionCard(
    session: Session,
    onClick: () -> Unit,
    onCheckInClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side - Session info with status indicator
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Status badge
                when (session.status) {
                    SessionStatus.LIVE_NOW -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE NOW",
                                color = SuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    SessionStatus.STARTS_SOON -> {
                        Text(
                            text = "STARTS IN ${session.startsIn}",
                            color = AccentBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    SessionStatus.UPCOMING -> {
                        Text(
                            text = "UPCOMING",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    SessionStatus.PAST -> {
                        // No status badge for past sessions
                    }
                }
                
                // Session title
                Text(
                    text = session.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Time info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(TextSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = session.timeRange,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Location info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pin),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(TextSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = session.location,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
                
                // Attendance info with Check In button for live sessions
                // Check In button is positioned on the RIGHT side, next to attendance deadline
                if (session.status == SessionStatus.LIVE_NOW && session.attendanceDeadline != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Attendance open until ${session.attendanceDeadline}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onCheckInClick,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(40.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Check In",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            
            // Right side - Session image (only for non-live sessions)
            if (session.status != SessionStatus.LIVE_NOW) {
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    SoftBlue.copy(alpha = 0.3f),
                                    SoftBlueDark.copy(alpha = 0.3f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    session.imageRes?.let { imageRes ->
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Fit
                        )
                    } ?: run {
                        Image(
                            painter = painterResource(id = R.drawable.graduation),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            colorFilter = ColorFilter.tint(AccentBlue)
                        )
                    }
                }
            }
        }
    }
}

// Data classes and enums

enum class SessionTab(val title: String) {
    TODAY("Today"),
    UPCOMING("Upcoming"),
    PAST("Past")
}

enum class SessionStatus {
    LIVE_NOW,      // From API: status == "live" or "active"
    STARTS_SOON,   // From API: status == "starting_soon"
    UPCOMING,      // From API: status == "upcoming" or "scheduled"
    PAST           // From API: status == "completed" or "past"
}

/**
 * Session Data Class
 * 
 *  ALL FIELDS ARE DYNAMIC - Populated from backend API
 * 
 * Map from API response:
 * ```
 * fun ApiSession.toSession() = Session(
 *     id = this.id,
 *     title = this.title,
 *     date = this.formattedDate,        // e.g., "Wednesday, Oct 25"
 *     timeRange = this.timeRange,       // e.g., "09:00 AM - 10:30 AM"
 *     location = this.location,         // e.g., "Room 101 • Science Building"
 *     status = when(this.status) {
 *         "live" -> SessionStatus.LIVE_NOW
 *         "starting_soon" -> SessionStatus.STARTS_SOON
 *         "upcoming" -> SessionStatus.UPCOMING
 *         "past" -> SessionStatus.PAST
 *         else -> SessionStatus.UPCOMING
 *     },
 *     startsIn = this.startsIn,         // e.g., "2H" (only for STARTS_SOON)
 *     attendanceDeadline = this.attendanceDeadline, // e.g., "09:15 AM"
 *     imageRes = null,                  // Optional: map to local drawable
 *     isToday = this.isToday
 * )
 * ```
 */
data class Session(
    val id: String,                    // From API: session.id
    val title: String,                 // From API: session.title
    val date: String,                  // From API: session.formattedDate
    val timeRange: String,             // From API: session.timeRange
    val location: String,              // From API: session.location
    val status: SessionStatus,         // From API: session.status (mapped)
    val startsIn: String? = null,      // From API: session.startsIn (optional)
    val attendanceDeadline: String? = null, // From API: session.attendanceDeadline (optional)
    val imageRes: Int? = null,         // Optional: local drawable resource
    val isToday: Boolean = false       // From API: session.isToday
)

/**
 *  SAMPLE DATA - FOR PREVIEW/TESTING ONLY
 * 
 * In production, replace with actual API call:
 * ```
 * val sessions = apiService.getSessions().sessions.map { it.toSession() }
 * ```
 * 
 *  All session cards are generated dynamically from backend
 * The number of cards, their content, and order are all determined by the API response.
 */
fun sampleSessions() = listOf(
    Session(
        id = "1",
        title = "Advanced Calculus",
        date = "Wednesday, Oct 25",
        timeRange = "09:00 AM - 10:30 AM",
        location = "Room 101 • Science Building",
        status = SessionStatus.LIVE_NOW,
        attendanceDeadline = "09:15 AM",
        isToday = true
    ),
    Session(
        id = "2",
        title = "History of Art",
        date = "Wednesday, Oct 25",
        timeRange = "11:00 AM • 90 min",
        location = "Lecture Hall B",
        status = SessionStatus.STARTS_SOON,
        startsIn = "2H",
        isToday = true
    ),
    Session(
        id = "3",
        title = "Physics 101",
        date = "Thursday, Oct 26",
        timeRange = "10:00 AM • Lab 3",
        location = "Lab 3",
        status = SessionStatus.UPCOMING
    ),
    Session(
        id = "4",
        title = "Intro to CS",
        date = "Thursday, Oct 26",
        timeRange = "02:00 PM • Room 304",
        location = "Room 304",
        status = SessionStatus.UPCOMING
    )
)
