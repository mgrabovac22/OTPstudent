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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prijava za praksu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
            ) {
                Text("Dalje")
            }
        }
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
                modifier = Modifier.padding(bottom = 8.dp)
            )

            uiState.jobsError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
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
                            onCheckedChange = { viewModel.toggleJobSelection(job.id) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = job.name)
                    }
                }
            }
        }
    }
}
