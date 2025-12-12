package hr.wortex.otpstudent.ui.nav

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeIconBitmap = assetToImageBitmap("otp_icon.png")
    val jobIconBitmap = assetToImageBitmap("job_icon.png")
    val profileIconBitmap = assetToImageBitmap("profile_icon.png")

    val routeToBitmapMap = mapOf(
        "business_screen" to jobIconBitmap,
        "home_screen" to homeIconBitmap,
        "profile_screen" to profileIconBitmap
    )

    val items = listOf(
        NavItem(route = "business_screen", label = "Poslovna zona", icon = Icons.Default.ThumbUp),
        NavItem(route = "home_screen", label = "", icon = Icons.Default.Home),
        NavItem(route = "profile_screen", label = "Profil", icon = Icons.Default.AccountCircle)
    )

    val CustomIndicatorColor = Color(0xFF2E7D32)

    NavigationBar(
        containerColor = Color(0xFF1B6E2A),
        tonalElevation = 0.dp
    ) {
        items.forEach { (route, label, _) ->
            val isHome = label == ""
            val selected = currentRoute == route
            val targetBoxSize = if (isHome) 72.dp else 48.dp
            val animatedBoxSize by animateDpAsState(targetBoxSize, label = "animatedBoxSize", animationSpec = tween(durationMillis = 300))

            val iconOffset by animateDpAsState(if (selected) (-14).dp else (-10).dp, label = "iconOffset", animationSpec = tween(durationMillis = 300))

            val currentIconBitmap = routeToBitmapMap[route]
            val iconColor = Color.White

            val indicatorSize by animateDpAsState(
                targetValue = if (selected) (if (isHome) 68.dp else 44.dp) else 0.dp,
                label = "indicatorSize",
                animationSpec = tween(durationMillis = 300)
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = false,
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(animatedBoxSize)
                            .offset(
                                y = if (isHome) {
                                    if (selected) (-10).dp else 0.dp
                                } else {
                                    iconOffset
                                }
                            )
                    ) {

                        Spacer(
                            modifier = Modifier
                                .size(indicatorSize)
                                .clip(CircleShape)
                                .background(CustomIndicatorColor)
                        )
                        if (currentIconBitmap != null) {
                            Image(
                                bitmap = currentIconBitmap,
                                contentDescription = label.ifEmpty { "Home" },
                                modifier = Modifier
                                    .size(if (isHome) 55.dp else 34.dp)
                                    .offset(y = if (isHome) 0.dp else 0.dp),
                                colorFilter = ColorFilter.tint(iconColor)
                            )
                        } else {
                            Text(
                                text = "?",
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                label = {
                    if (!isHome){
                        Text(
                            text = label,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = if (isHome) Color(0xFF1B6E2A) else CustomIndicatorColor
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BottomNavigationBar(navController = navController)
}