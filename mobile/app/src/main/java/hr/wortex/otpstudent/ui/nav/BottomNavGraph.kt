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
import hr.wortex.otpstudent.ui.profil.EditProfileScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import hr.wortex.otpstudent.ui.sustav_nagardivanja.informativni_sadrzaj.InfoContentScreen
import hr.wortex.otpstudent.ui.sustav_nagardivanja.informativni_sadrzaj.InfoDetailScreen
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
                HomeScreen(innerPadding, onRewardClick = {navController.navigate("reward_screen")})
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
                EditProfileScreen()
            }
            composable("unlock_screen") {
                UnlockScreen(navController = navController)
            }

            composable("reward_screen") {
                InfoContentScreen(paddingValues = innerPadding, navController = navController)
            }

            composable(
                route = "info_detail_screen/{contentId}",
                arguments = listOf(navArgument("contentId") { type = NavType.IntType })
            ) { backStackEntry ->
                val contentId = backStackEntry.arguments?.getInt("contentId") ?: 0

                InfoDetailScreen(contentId = contentId, navController = navController)
            }
        }
    }
}