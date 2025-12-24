package com.example.studentattandance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentattandance.R
import com.example.studentattandance.ui.theme.StudentAttandanceTheme
import com.example.studentattandance.ui.theme.ExclusionRed
import com.example.studentattandance.ui.theme.SuccessCardGreen
import com.example.studentattandance.ui.theme.SuccessGreen
import com.example.studentattandance.ui.theme.TextPrimary
import com.example.studentattandance.ui.theme.WarningOrange

/**
 * Attendance State Enum
 * Determines which card variant to display based on attendance status
 */
enum class AttendanceState {
    SUCCESS,    // No absences - Green card
    WARNING,    // 2-4 absences - Orange card
    EXCLUSION   // 5+ absences - Red card
}

/**
 * Data class to hold card configuration
 */
private data class CardConfig(
    val cardColor: Color,
    val iconColor: Color,
    val iconRes: Int,
    val title: String,
    val message: String,
    val buttonText: String,
    val buttonColor: Color
)

/**
 * Attendance Card Component
 * 
 * A unified card component that displays different states based on student attendance:
 * - Green: Great attendance (no absences)
 * - Orange: Warning (2-4 absences in a module)
 * - Red: Exclusion (5+ absences in a module)
 * 
 * All values are dynamic and can be updated from API responses.
 * 
 * @param state Attendance state (from API: calculate based on missedCount)
 * @param courseName Course/module name (from API: attendance.courseName or null for success state)
 * @param missedCount Number of missed classes (from API: attendance.missedCount)
 * @param onViewReportClick Callback for "View Report" button (from API: handle navigation)
 * 
 * Example API usage:
 * ```
 * // Success state (no absences)
 * AttendanceCard(
 *     state = AttendanceState.SUCCESS,
 *     courseName = null,
 *     missedCount = 0,
 *     onViewReportClick = { navController.navigate("report") }
 * )
 * 
 * // Warning state (2-4 absences)
 * AttendanceCard(
 *     state = AttendanceState.WARNING,
 *     courseName = apiResponse.warnings[0].courseName,
 *     missedCount = apiResponse.warnings[0].missedCount,
 *     onViewReportClick = { navController.navigate("warningDetails") }
 * )
 * 
 * // Exclusion state (5+ absences)
 * AttendanceCard(
 *     state = AttendanceState.EXCLUSION,
 *     courseName = apiResponse.exclusions[0].courseName,
 *     missedCount = apiResponse.exclusions[0].missedCount,
 *     onViewReportClick = { navController.navigate("exclusionDetails") }
 * )
 * ```
 */
@Composable
fun AttendanceCard(
    state: AttendanceState,
    courseName: String? = null,
    missedCount: Int = 0,
    onViewReportClick: () -> Unit = { /* TODO: Navigate to report */ }
) {
    val cardConfig = when (state) {
        AttendanceState.SUCCESS -> CardConfig(
            cardColor = SuccessCardGreen,
            iconColor = SuccessGreen,
            iconRes = R.drawable.check,
            title = "Great attendance!",
            message = "No absences recorded so far. You're on track with all your classes!",
            buttonText = "View Report",
            buttonColor = SuccessGreen
        )
        AttendanceState.WARNING -> CardConfig(
            cardColor = WarningOrange.copy(alpha = 0.15f),
            iconColor = WarningOrange,
            iconRes = R.drawable.warning_sign,
            title = "Attendance Warning",
            message = "You have missed $missedCount classes in ${courseName ?: "this module"}. Risk of penalty on next absence.",
            buttonText = "View Details",
            buttonColor = WarningOrange
        )
        AttendanceState.EXCLUSION -> CardConfig(
            cardColor = ExclusionRed.copy(alpha = 0.15f),
            iconColor = ExclusionRed,
            iconRes = R.drawable.warning_sign,
            title = "Exclusion Warning",
            message = "You have been excluded from ${courseName ?: "this session"} due to $missedCount absences.",
            buttonText = "View Report",
            buttonColor = ExclusionRed
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardConfig.cardColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardConfig.iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = cardConfig.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(cardConfig.iconColor)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cardConfig.title,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cardConfig.message,
                        color = TextPrimary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onViewReportClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cardConfig.buttonColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = cardConfig.buttonText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceCardSuccessPreview() {
    StudentAttandanceTheme {
        AttendanceCard(
            state = AttendanceState.SUCCESS,
            onViewReportClick = { /* TODO */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceCardWarningPreview() {
    StudentAttandanceTheme {
        AttendanceCard(
            state = AttendanceState.WARNING,
            courseName = "Math 101",
            missedCount = 2,
            onViewReportClick = { /* TODO */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceCardExclusionPreview() {
    StudentAttandanceTheme {
        AttendanceCard(
            state = AttendanceState.EXCLUSION,
            courseName = "Physics 201",
            missedCount = 5,
            onViewReportClick = { /* TODO */ }
        )
    }
}

