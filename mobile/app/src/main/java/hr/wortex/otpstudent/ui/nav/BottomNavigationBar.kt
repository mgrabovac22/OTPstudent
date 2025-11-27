package hr.wortex.otpstudent.ui.nav

import androidx.compose.material3.Icon
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        NavItem(route = "business_screen", label = "Poslovna zona", icon = Icons.Default.ThumbUp),
        NavItem(route = "home_screen", label = "", icon = Icons.Default.Home),
        NavItem(route = "profile_screen", label = "Profil", icon = Icons.Default.AccountCircle)
    )

    NavigationBar(
        containerColor = Color(0xFF1B6E2A)
    ) {
        items.forEach { (route, label, icon) ->
            val isHome = label == ""
            val selected = currentRoute == route
            val iconOffset by animateDpAsState(if (selected) (-14).dp else (-10).dp)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    // očuvanje stanja ekrana (ne kraira novu istancu svaki puta)!
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
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(if (isHome) 72.dp else 48.dp) // veći container
                            .offset(y = if (isHome) (-10).dp else iconOffset)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(if (isHome) 60.dp else 34.dp)
                        )
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
                    indicatorColor = Color(0xFF2E7D32)
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
