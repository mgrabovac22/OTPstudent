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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.wortex.core.UnlockMethodRegistry
import hr.wortex.core.UnlockResult
import hr.wortex.otpstudent.di.DependencyProvider

@Composable
fun UnlockScreen(navController: NavController) {
    val viewModel: UnlockViewModel = viewModel(factory = DependencyProvider.unlockViewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var generalError by remember { mutableStateOf<String?>(null) }

    // TODO: fix this hack of sorting methods by names
    val unlockMethods = UnlockMethodRegistry.methods.sortedBy { it.name }

    LaunchedEffect(unlockMethods.size) {
        if (unlockMethods.isNotEmpty()) {
            val activity = context as? FragmentActivity
            if (activity == null) {
                generalError = "Greška: Nije moguće dohvatiti aktivnost."
                return@LaunchedEffect
            }

            var lastError : String? = null

            // Loop through all registered unlock methods sequentially
            for (method in unlockMethods) {
                val result = method.launchUnlock(
                    activity = activity,
                    checkHasTokens = { DependencyProvider.authRepository.hasSavedTokens() }
                )

                if (result is UnlockResult.Success) {
                    viewModel.verifySession()
                    lastError = null
                    break
                } else if (result is UnlockResult.Error) {
                    lastError = result.message
                }
            }

            generalError = lastError
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Waiting for Initializer to run
        if (unlockMethods.isEmpty() && generalError == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading unlock methods...")
            }
        }

        // ViewModel is actively verifying the session.
        if (uiState is UnlockUiState.Loading) {
            CircularProgressIndicator()
        }

        // An error occurred from any source.
        generalError?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }

    // Side-effects for navigation
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
}
