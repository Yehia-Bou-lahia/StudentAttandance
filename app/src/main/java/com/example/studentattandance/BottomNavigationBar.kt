package com.example.studentattandance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentattandance.ui.theme.AccentBlue
import com.example.studentattandance.ui.theme.DeepNavy
import com.example.studentattandance.ui.theme.SoftBlue
import com.example.studentattandance.ui.theme.SoftBlueDark
import com.example.studentattandance.ui.theme.TextPrimary
import com.example.studentattandance.ui.theme.TextSecondary

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit = {},
    onSessionsClick: () -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calendar,
        BottomNavItem.Scan,
        BottomNavItem.History,
        BottomNavItem.Profile
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DeepNavy)
    ) {
        // Navigation Bar
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = DeepNavy,
            contentColor = TextPrimary,
            tonalElevation = 0.dp
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItem == index
                
                if (item == BottomNavItem.Scan) {
                    // Empty space for floating button
                    Box(modifier = Modifier.weight(1f))
                } else {
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { 
                            selectedItem = index
                            if (item == BottomNavItem.Calendar) {
                                onSessionsClick()
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp),
                                tint = if (item == BottomNavItem.Home) {
                                    Color.Unspecified // Keep original color for Home icon
                                } else {
                                    if (isSelected) AccentBlue else TextSecondary
                                }
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                color = if (isSelected) AccentBlue else TextSecondary,
                                fontWeight = if(isSelected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (item == BottomNavItem.Home) Color.Unspecified else AccentBlue,
                            selectedTextColor = AccentBlue,
                            unselectedIconColor = if (item == BottomNavItem.Home) Color.Unspecified else TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
        
        // Floating QR Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow effect
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .offset(y = (-12).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SoftBlue.copy(alpha = 0.35f),
                                SoftBlue.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            // Main button
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .offset(y = (-12).dp)
                    .shadow(14.dp, CircleShape, spotColor = SoftBlue.copy(alpha = 0.6f))
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(SoftBlue, SoftBlueDark)
                        )
                    )
                    .clickable { 
                        selectedItem = 2
                        onScanClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.qr),
                    contentDescription = "Scan",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

sealed class BottomNavItem(var title: String, var iconRes: Int) {
    object Home : BottomNavItem("Home", R.drawable.home)
    object Calendar : BottomNavItem("Sessions", R.drawable.calendar)
    object Scan : BottomNavItem("Scan", R.drawable.qr)
    object History : BottomNavItem("History", R.drawable.time)
    object Profile : BottomNavItem("Profile", R.drawable.user)
}
