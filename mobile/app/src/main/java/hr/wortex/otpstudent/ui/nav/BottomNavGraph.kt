package hr.wortex.otpstudent.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.wortex.otpstudent.ui.home.HomeScreen
import hr.wortex.otpstudent.ui.poslovi.BusinessScreen
import hr.wortex.otpstudent.ui.profil.ProfileScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("business_screen") {
                BusinessScreen()
            }
            composable("home_screen") {
                HomeScreen()
            }
            composable("profile_screen") {
                ProfileScreen()
            }
        }
    }
}