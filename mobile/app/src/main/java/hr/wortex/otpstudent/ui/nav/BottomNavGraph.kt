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
import hr.wortex.otpstudent.ui.editProfile.EditProfileScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import hr.wortex.otpstudent.ui.unlock.UnlockScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != "login_screen" && currentRoute != "unlock_screen"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "unlock_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("business_screen") {
                BusinessScreen()
            }
            composable("home_screen") {
                HomeScreen(innerPadding)
            }
            composable("profile_screen") {
                ProfileScreen(onEditProfile = {
                    navController.navigate("edit_profile_screen")
                })
            }
            composable("login_screen") {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(paddingValues = innerPadding, navController = navController)
                }
            }
            composable("edit_profile_screen") {
                EditProfileScreen(
                    padding = innerPadding,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("unlock_screen") {
                UnlockScreen(navController = navController)
            }
        }
    }
}