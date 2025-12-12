package hr.wortex.otpstudent.ui.internship

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipJobsScreen(
    navController: NavController,
    viewModel: InternshipApplicationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val availableJobs = uiState.availableJobs
    val selectedJobIds = uiState.selectedJobIds

    val AppGreen = Color(0xFF1B6E2A)
    val AppOrange = Color(0xFFf2701b)
    val White = Color.White
    val Black = Color.Black
    val RedError = MaterialTheme.colorScheme.error

    val checkboxColors = CheckboxDefaults.colors(
        checkedColor = AppGreen,
        uncheckedColor = AppGreen,
        checkmarkColor = White
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prijava za praksu", fontWeight = FontWeight.Bold, color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppGreen)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (viewModel.validateJobsStep()) {
                        navController.navigate("internship_details_screen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppOrange, contentColor = White)
            ) {
                Text("Dalje")
            }
        },
        containerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "U kojem biste području unutar OTP banke željeli obavljati praksu?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = AppGreen
            )

            uiState.jobsError?.let {
                Text(
                    text = it,
                    color = RedError,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            LazyColumn {
                items(availableJobs) { job ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleJobSelection(job.id) }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selectedJobIds.contains(job.id),
                            onCheckedChange = { viewModel.toggleJobSelection(job.id) },
                            colors = checkboxColors
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = job.name, color = Black)
                    }
                }
            }
        }
    }
}