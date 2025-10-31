package hr.wortex.otpstudent.ui.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.wortex.otpstudent.ui.home.HomeScreen
import hr.wortex.otpstudent.ui.login.LoginScreen
import hr.wortex.otpstudent.ui.poslovi.BusinessScreen
import hr.wortex.otpstudent.ui.profil.ProfileScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue

@Composable
fun MainNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != "login_screen"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login_screen",
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
            composable("login_screen") {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(paddingValues = innerPadding, navController = navController)
                }
            }
        }
    }
}