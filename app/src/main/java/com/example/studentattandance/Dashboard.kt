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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
 * 
 * Example API usage:
 * ```
 * // After API call:
 * StudentDashboardScreen(
 *     userName = apiResponse.user.name,
 *     totalClasses = apiResponse.attendance.total,
 *     presentClasses = apiResponse.attendance.present,
 *     absentClasses = apiResponse.attendance.absent,
 *     attendancePercentage = apiResponse.attendance.percentage, // Optional, will calculate if not provided
 *     encouragementMessage = apiResponse.attendance.message // Optional
 * )
 * ```
 */
@Composable
fun StudentDashboardScreen(
    userName: String = "yehia",
    totalClasses: Int = 45,
    presentClasses: Int = 38,
    absentClasses: Int = 7,
    attendancePercentage: Int = -1, // -1 means calculate from presentClasses/totalClasses
    encouragementMessage: String = "You are doing well! Keep up the consistency."
) {
    // Calculate attendance percentage if not provided
    val calculatedPercentage = if (attendancePercentage == -1 && totalClasses > 0) {
        ((presentClasses.toFloat() / totalClasses.toFloat()) * 100).toInt()
    } else {
        attendancePercentage.coerceIn(0, 100)
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DeepNavy
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun StudentDashboardPreview() {
    StudentAttandanceTheme {
        StudentDashboardScreen()
    }
}

