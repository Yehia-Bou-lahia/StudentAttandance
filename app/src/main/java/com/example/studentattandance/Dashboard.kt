package com.example.studentattandance

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentattandance.R
import com.example.studentattandance.ui.theme.StudentAttandanceTheme
import com.example.studentattandance.ui.theme.AccentBlue
import com.example.studentattandance.ui.theme.CardNavy
import com.example.studentattandance.ui.theme.DeepNavy
import com.example.studentattandance.ui.theme.MutedRed
import com.example.studentattandance.ui.theme.SoftBlue
import com.example.studentattandance.ui.theme.SoftBlueDark
import com.example.studentattandance.ui.theme.SuccessGreen
import com.example.studentattandance.ui.theme.TextPrimary

/**
 * Student Dashboard Screen - Displays attendance information and statistics
 * 
 * All parameters are dynamic and can be updated from API responses:
 * 
 * @param userName Student's name (from API: user.name or user.username)
 * @param totalClasses Total number of classes (from API: attendance.total)
 * @param presentClasses Number of present classes (from API: attendance.present)
 * @param absentClasses Number of absent classes (from API: attendance.absent)
 * @param attendancePercentage Overall attendance percentage. If -1, calculates from presentClasses/totalClasses.
 *                             Can be provided directly from API (from API: attendance.percentage)
 * @param encouragementMessage Motivational message (from API: attendance.message or generate based on percentage)
 * @param attendanceCourseName Course/module name (from API: attendance.courseName or null for success state)
 * @param attendanceMissedCount Number of missed classes in a specific module (from API: attendance.missedCount)
 *                              State is automatically calculated:
 *                              - 0 absences = SUCCESS (green card)
 *                              - 2-4 absences = WARNING (orange card)
 *                              - 5+ absences = EXCLUSION (red card)
 * @param onViewReportClick Callback for "View Report/Details" button (from API: handle navigation)
 * @param upNextClasses List of upcoming classes (from API: schedule.upcoming)
 *                      Default: sample data for preview. Replace with API data:
 *                      apiResponse.schedule.upcoming.map { classItem ->
 *                          UpNextClass(
 *                              time = classItem.startTime,           // "09:00 AM"
 *                              moduleName = classItem.moduleName,     // "Introduction to Physics"
 *                              room = classItem.room,                // "Room 302"
 *                              duration = classItem.duration,        // "1h 30m"
 *                              checkedInTime = classItem.checkedInTime // "Checked in at 8:55 AM" or null
 *                          )
 *                      }
 * @param onSeeAllClick Callback for "See all" link (from API: handle navigation to full schedule)
 * @param onClassClick Callback when a class card is clicked (from API: handle navigation to class details)
 * 
 * Example API usage:
 * ```
 * // After API call - all values are dynamic:
 * StudentDashboardScreen(
 *     userName = apiResponse.user.name,
 *     totalClasses = apiResponse.attendance.total,
 *     presentClasses = apiResponse.attendance.present,
 *     absentClasses = apiResponse.attendance.absent,
 *     attendancePercentage = apiResponse.attendance.percentage, // Optional, will calculate if not provided
 *     encouragementMessage = apiResponse.attendance.message, // Optional
 *     attendanceCourseName = apiResponse.attendance.courseName,
 *     attendanceMissedCount = apiResponse.attendance.missedCount, // State calculated automatically
 *     onViewReportClick = { navController.navigate("report") },
 *     upNextClasses = apiResponse.schedule.upcoming.map { classItem ->
 *         UpNextClass(
 *             time = classItem.startTime,
 *             moduleName = classItem.moduleName,
 *             room = classItem.room,
 *             duration = classItem.duration,
 *             checkedInTime = classItem.checkedInTime
 *         )
 *     },
 *     onSeeAllClick = { navController.navigate("schedule") },
 *     onClassClick = { classItem -> navController.navigate("classDetails/${classItem.id}") }
 * )
 * ```
 */
@Composable
fun StudentDashboardScreen(
    userName: String = "yehia",
    totalClasses: Int = 45,
    presentClasses: Int = 45,
    absentClasses: Int = 0,
    attendancePercentage: Int = -1, // -1 means calculate from presentClasses/totalClasses
    encouragementMessage: String = "You are doing well! Keep up the consistency.",
    attendanceCourseName: String? = null,
    attendanceMissedCount: Int = 0,
    onViewReportClick: () -> Unit = { /* TODO: Navigate to report */ },
    upNextClasses: List<UpNextClass> = sampleUpNextClasses(),
    onSeeAllClick: () -> Unit = { /* TODO: Navigate to full schedule */ },
    onClassClick: (UpNextClass) -> Unit = { /* TODO: Navigate to class details */ },
    onScanClick: () -> Unit = { /* TODO: Navigate to QR scanner */ },
    onSessionsClick: () -> Unit = { /* TODO: Navigate to sessions */ }
) {
    // Calculate attendance percentage if not provided
    val calculatedPercentage = if (attendancePercentage == -1 && totalClasses > 0) {
        ((presentClasses.toFloat() / totalClasses.toFloat()) * 100).toInt()
    } else {
        attendancePercentage.coerceIn(0, 100)
    }
    
    // Automatically calculate attendance state based on missed count
    val calculatedState = when {
        attendanceMissedCount >= 5 -> AttendanceState.EXCLUSION
        attendanceMissedCount >= 2 -> AttendanceState.WARNING
        else -> AttendanceState.SUCCESS
    }
    
    Scaffold(
        bottomBar = { 
            BottomNavigationBar(
                onScanClick = onScanClick,
                onSessionsClick = onSessionsClick
            ) 
        },
        containerColor = DeepNavy
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = DeepNavy
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                DashboardHeader(userName = userName)
                Spacer(modifier = Modifier.height(16.dp))
                OverallAttendanceCard(
                    attendancePercentage = calculatedPercentage,
                    encouragementMessage = encouragementMessage
                )
                Spacer(modifier = Modifier.height(16.dp))
                AttendanceStatsRow(
                    total = totalClasses,
                    present = presentClasses,
                    absent = absentClasses
                )
                
                // Show attendance card - state is automatically calculated from missedCount
                Spacer(modifier = Modifier.height(16.dp))
                AttendanceCard(
                    state = calculatedState,
                    courseName = attendanceCourseName,
                    missedCount = attendanceMissedCount,
                    onViewReportClick = onViewReportClick
                )
                
                // Up Next Section
                Spacer(modifier = Modifier.height(16.dp))
                UpNextSection(
                    classes = upNextClasses,
                    onSeeAllClick = onSeeAllClick,
                    onClassClick = onClassClick
                )
                
                // Bottom padding for scroll
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Data class for Up Next class information
 * 
 * All values are dynamic and can be updated from API responses.
 * This data class maps directly to API response structure.
 * 
 * @param time Class start time (from API: class.startTime or class.time)
 *             Format: "09:00 AM" or "HH:mm AM/PM"
 * @param moduleName Module/course name (from API: class.moduleName or class.courseName)
 * @param room Room location (from API: class.room or class.location)
 * @param duration Class duration (from API: class.duration)
 *                 Format: "1h 30m" or "HHh MMm"
 * @param checkedInTime Checked-in status message (from API: class.checkedInTime)
 *                      Format: "Checked in at 8:55 AM" or null if not checked in
 * 
 * Example API mapping:
 * ```
 * UpNextClass(
 *     time = apiClass.startTime,              // "09:00 AM"
 *     moduleName = apiClass.moduleName,       // "Introduction to Physics"
 *     room = apiClass.room,                   // "Room 302"
 *     duration = apiClass.duration,           // "1h 30m"
 *     checkedInTime = apiClass.checkedInTime  // "Checked in at 8:55 AM" or null
 * )
 * ```
 */
data class UpNextClass(
    val time: String,              // e.g., "09:00 AM" (from API: class.startTime)
    val moduleName: String,        // e.g., "Introduction to Physics" (from API: class.moduleName)
    val room: String,               // e.g., "Room 302" (from API: class.room)
    val duration: String,           // e.g., "1h 30m" (from API: class.duration)
    val checkedInTime: String? = null // e.g., "Checked in at 8:55 AM" (from API: class.checkedInTime or null)
)

@Composable
fun OverallAttendanceCard(
    attendancePercentage: Int,
    encouragementMessage: String = "You are doing well! Keep up the consistency."
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SoftBlue, SoftBlueDark)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Overall Attendance",
                        color = TextPrimary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$attendancePercentage%",
                        color = TextPrimary,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.graduation),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            colorFilter = ColorFilter.tint(TextPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "On Track",
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = encouragementMessage,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Animated Circular Progress Indicator
                AnimatedCircularProgress(
                    percentage = attendancePercentage,
                    modifier = Modifier.size(110.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardHeader(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello There,",
                color = TextPrimary.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Hi, $userName 👋",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.notification_bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(TextPrimary)
            )
        }
    }
}

@Composable
fun AnimatedCircularProgress(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    var targetProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(percentage) {
        targetProgress = percentage / 100f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1500),
        label = "progress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background circle
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweepAngle = 360 * animatedProgress
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }

        // Checkmark icon in center
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.check),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(TextPrimary)
            )
        }
    }
}

@Composable
fun AttendanceStatsRow(
    total: Int,
    present: Int,
    absent: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "TOTAL",
            value = total,
            iconRes = R.drawable.graduation,
            iconColor = AccentBlue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "PRESENT",
            value = present,
            iconRes = R.drawable.check,
            iconColor = SuccessGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "ABSENT",
            value = absent,
            iconRes = R.drawable.wrong,
            iconColor = MutedRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: Int,
    iconRes: Int,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(iconColor)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value.toString(),
                color = TextPrimary,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = TextPrimary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Up Next Section Component
 * 
 * Displays a list of upcoming classes with dynamic values from API.
 * All values are passed through UpNextClass data class which maps to API response.
 * 
 * @param classes List of upcoming classes (from API: schedule.upcoming)
 * @param onSeeAllClick Callback for "See all" link (from API: handle navigation)
 * @param onClassClick Callback when a class card is clicked (from API: handle navigation)
 */
@Composable
fun UpNextSection(
    classes: List<UpNextClass>,
    onSeeAllClick: () -> Unit,
    onClassClick: (UpNextClass) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Up Next",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = onSeeAllClick) {
                Text(
                    text = "See all",
                    color = AccentBlue,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            classes.forEach { classItem ->
                UpNextCard(
                    classItem = classItem,
                    onClick = { onClassClick(classItem) }
                )
            }
        }
    }
}

/**
 * Up Next Card Component
 * 
 * Displays a single upcoming class card with all dynamic values:
 * - Time (from classItem.time)
 * - Module name (from classItem.moduleName)
 * - Room (from classItem.room)
 * - Duration (from classItem.duration)
 * - Checked-in status (from classItem.checkedInTime, optional)
 * 
 * All values come from UpNextClass data class which maps to API response.
 * 
 * @param classItem UpNextClass object containing all class information (from API)
 * @param onClick Callback when card is clicked (from API: handle navigation)
 */
@Composable
fun UpNextCard(
    classItem: UpNextClass,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 14.dp)
            ) {
                Text(
                    text = classItem.time.split(" ")[0], // "09:00"
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = classItem.time.split(" ").getOrElse(1) { "" }, // "AM"
                    color = TextPrimary.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }
            
            // Divider
            Divider(
                modifier = Modifier
                    .height(52.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.08f)
            )
            
            Spacer(modifier = Modifier.width(14.dp))
            
            // Class Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = classItem.moduleName,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Room
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.pin),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(TextPrimary.copy(alpha = 0.7f))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = classItem.room,
                            color = TextPrimary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // Duration
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(TextPrimary.copy(alpha = 0.7f))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = classItem.duration,
                            color = TextPrimary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                // Checked in status
                classItem.checkedInTime?.let { checkedIn ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = checkedIn,
                            color = SuccessGreen,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
            
            // Arrow Icon
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "View Details",
                    tint = TextPrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Sample data for Up Next classes
 * 
 * This is ONLY used as default/preview data.
 * In production, replace with API data by passing upNextClasses parameter:
 * 
 * ```
 * StudentDashboardScreen(
 *     // ... other params
 *     upNextClasses = apiResponse.schedule.upcoming.map { apiClass ->
 *         UpNextClass(
 *             time = apiClass.startTime,              // Dynamic from API
 *             moduleName = apiClass.moduleName,       // Dynamic from API
 *             room = apiClass.room,                   // Dynamic from API
 *             duration = apiClass.duration,           // Dynamic from API
 *             checkedInTime = apiClass.checkedInTime  // Dynamic from API (null if not checked in)
 *         )
 *     }
 * )
 * ```
 */
private fun sampleUpNextClasses() = listOf(
    UpNextClass(
        time = "09:00 AM",
        moduleName = "Introduction to Physics",
        room = "Room 302",
        duration = "1h 30m"
    ),
    UpNextClass(
        time = "11:30 AM",
        moduleName = "World History",
        room = "Room 105",
        duration = "1h 00m"
    ),
    UpNextClass(
        time = "08:00 AM",
        moduleName = "Chemistry Lab",
        room = "Room 201",
        duration = "2h 00m",
        checkedInTime = "Checked in at 8:55 AM"
    )
)

@Preview(showBackground = true)
@Composable
fun StudentDashboardPreview() {
    StudentAttandanceTheme {
        StudentDashboardScreen(
            attendanceCourseName = "Math 101",
            attendanceMissedCount = 2 // Will automatically show WARNING state
        )
    }
}

