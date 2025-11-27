package hr.wortex.otpstudent.ui.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.ui.editProfile.EditProfileScreen
import hr.wortex.otpstudent.ui.home.HomeScreen
import hr.wortex.otpstudent.ui.internship.InternshipApplicationViewModel
import hr.wortex.otpstudent.ui.internship.InternshipDetailsScreen
import hr.wortex.otpstudent.ui.internship.InternshipIntroScreen
import hr.wortex.otpstudent.ui.internship.InternshipJobsScreen
import hr.wortex.otpstudent.ui.internship.InternshipUserDataScreen
import hr.wortex.otpstudent.ui.login.LoginScreen
import hr.wortex.otpstudent.ui.career.CareerScreen
import hr.wortex.otpstudent.ui.profil.ProfileScreen
import hr.wortex.otpstudent.ui.registration.RegistrationScreen
import hr.wortex.otpstudent.ui.unlock.UnlockScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != "login_screen" && currentRoute != "unlock_screen" && currentRoute != "registration_screen"

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
                CareerScreen(navController = navController)
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

            composable("registration_screen") {
                RegistrationScreen(paddingValues = innerPadding, navController = navController)
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

            internshipApplicationGraph(navController)
        }
    }
}

fun NavGraphBuilder.internshipApplicationGraph(navController: NavHostController) {
    navigation(startDestination = "internship_intro_screen", route = "internship_application_graph") {
        composable("internship_intro_screen") {
            InternshipIntroScreen(navController)
        }
        composable("internship_user_data_screen") {
            val backStackEntry = remember(it) { navController.getBackStackEntry("internship_application_graph") }
            val viewModel: InternshipApplicationViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = DependencyProvider.internshipApplicationViewModelFactory
            )
            InternshipUserDataScreen(navController, viewModel)
        }
        composable("internship_jobs_screen") {
            val backStackEntry = remember(it) { navController.getBackStackEntry("internship_application_graph") }
            val viewModel: InternshipApplicationViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = DependencyProvider.internshipApplicationViewModelFactory
            )
            InternshipJobsScreen(navController, viewModel)
        }
        composable("internship_details_screen") {
            val backStackEntry = remember(it) { navController.getBackStackEntry("internship_application_graph") }
            val viewModel: InternshipApplicationViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = DependencyProvider.internshipApplicationViewModelFactory
            )
            InternshipDetailsScreen(navController, viewModel)
        }
    }
}