package hr.wortex.otpstudent.ui.poslovi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.wortex.otpstudent.di.DependencyProvider

@Composable
fun BusinessScreen() {
    val viewModel: BusinessViewModel = viewModel(factory = DependencyProvider.businessViewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    var generalError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is BusinessUiState.Success -> {
                successMessage = "Prijava uspješna!"
                generalError = null
                kotlinx.coroutines.delay(3000)
                viewModel.resetState()
            }
            is BusinessUiState.Error -> {
                generalError = (uiState as BusinessUiState.Error).message
                successMessage = null
                kotlinx.coroutines.delay(3000)
                viewModel.resetState()
            }
            is BusinessUiState.Idle -> {
                generalError = null
                successMessage = null
            }
            else -> { /* Loading state is handled by the button */ }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                viewModel.applyForInternship()
            },
            enabled = uiState !is BusinessUiState.Loading
        ) {
            if (uiState is BusinessUiState.Loading) {
                CircularProgressIndicator()
            } else {
                Text("Prijava na Praksu")
            }
        }

        generalError?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        successMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = Color(0xFF056f52), // Using a success color from your theme
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}
