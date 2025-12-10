package hr.wortex.otpstudent.ui.career

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
fun StudentJobDetailsScreen(
    navController: NavController,
    viewModel: StudentJobDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.applySuccess) {
        when (uiState.applySuccess) {
            true -> snackbarHostState.showSnackbar("Prijava je uspješno poslana.")
            false -> snackbarHostState.showSnackbar("Prijava nije uspjela.")
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.job?.name ?: "Detalji posla",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Natrag",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.error ?: "Greška")
                }
            }

            uiState.job != null -> {
                StudentJobDetailsContent(
                    modifier = Modifier.padding(padding),
                    title = uiState.job!!.name,
                    location = uiState.job!!.city,
                    description = uiState.job!!.description,
                    onApplyClick = { viewModel.apply() },
                    isApplying = uiState.isApplying,
                    isApplied = uiState.isApplied
                )
            }
        }
    }
}

@Composable
private fun StudentJobDetailsContent(
    modifier: Modifier = Modifier,
    title: String,
    location: String,
    description: String,
    onApplyClick: () -> Unit,
    isApplying: Boolean,
    isApplied: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF66BB6A))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = location, color = Color.White, fontSize = 14.sp)
        }

        Column(
            modifier = Modifier
                .padding(12.dp)
                .background(Color(0xFF81C784), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                color = Color.White,
                fontSize = 13.sp
            )
        }

        Button(
            onClick = onApplyClick,
            enabled = !isApplying && !isApplied,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
        ) {
            when {
                isApplying -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
                isApplied -> {
                    Text("PRIJAVLJENI STE", color = Color.White, fontSize = 15.sp)
                }
                else -> {
                    Text("PRIJAVI SE", color = Color.White, fontSize = 15.sp)
                }
            }
        }
    }
}