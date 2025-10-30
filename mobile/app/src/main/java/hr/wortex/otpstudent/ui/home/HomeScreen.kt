package hr.wortex.otpstudent.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.usecase.GetUser

@Composable
fun HomeScreen() {
    val getUserUseCase = remember { GetUser(DependencyProvider.userRepository) }
    val viewModel = remember { HomeViewModel(getUserUseCase) }
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }

            is HomeUiState.Success -> {
                val user = state.user
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "👋 ${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📧 ${user.email}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🎓 ${user.areaOfStudy} - ${user.yearOfStudy}. godina")
                }
            }

            is HomeUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❌ ${state.message}", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(onClick = { viewModel.fetchCurrentUser() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry")
                    }
                }
            }
        }
    }
}
