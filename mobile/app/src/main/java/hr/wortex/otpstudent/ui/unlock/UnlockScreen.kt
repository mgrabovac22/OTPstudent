package hr.wortex.otpstudent.ui.unlock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.wortex.unlock.UnlockScreen as ModularUnlocker
import hr.wortex.otpstudent.di.DependencyProvider

@Composable
fun UnlockScreen(navController: NavController) {
    val viewModel: UnlockViewModel = viewModel(factory = DependencyProvider.unlockViewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    var generalError by remember { mutableStateOf<String?>(null) }

    // Use the modular unlocker
    ModularUnlocker(
        onUnlockSuccess = { viewModel.verifySession() },
        onSessionError = { errorMsg -> generalError = errorMsg },
        checkHasTokens = { DependencyProvider.authRepository.hasSavedTokens() }
    )

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UnlockUiState.Success -> {
                navController.navigate("home_screen") {
                    popUpTo("unlock_screen") { inclusive = true }
                }
            }
            is UnlockUiState.Error -> {
                 generalError = state.message
            }
            else -> { /* Idle or Loading */ }
        }
    }

    LaunchedEffect(generalError) {
        if (generalError != null) {
            navController.navigate("login_screen") {
                popUpTo("unlock_screen") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState is UnlockUiState.Loading) {
                CircularProgressIndicator()
            }

            generalError?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
