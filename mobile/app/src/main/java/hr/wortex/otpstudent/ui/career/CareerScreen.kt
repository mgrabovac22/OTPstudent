package hr.wortex.otpstudent.ui.career

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CareerScreen(navController: NavController) {
    navController.navigate("internship_application_graph")
}