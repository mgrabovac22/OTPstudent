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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
            true -> snackbarHostState.showSnackbar("Akcija je uspješno izvršena.")
            false -> snackbarHostState.showSnackbar("Akcija nije uspjela.")
            null -> Unit
        }
    }

    val title = uiState.job?.name ?: ""
    val location = uiState.job?.city ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1B6E2A)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(end = 56.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = location,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B6E2A)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
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
                    Text(text = uiState.error ?: "")
                }
            }

            uiState.job != null -> {
                StudentJobDetailsContent(
                    modifier = Modifier.padding(padding),
                    description = uiState.job!!.description,
                    onApplyClick = { viewModel.toggleApplication() },
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
        Column(
            modifier = Modifier
                .padding(12.dp)
                .background(Color(0xFF4CAF50), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Opis posla",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
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
            enabled = !isApplying,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isApplied) Color(0xFFD32F2F) else Color(0xFF2E7D32)
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
                    Text("ODJAVI SE", color = Color.White, fontSize = 15.sp)
                }
                else -> {
                    Text("PRIJAVI SE", color = Color.White, fontSize = 15.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentJobDetailsPreview() {
    StudentJobDetailsContent(
        description = "Tražimo studenta za rad na razvoju nove mobilne aplikacije. Poželjno poznavanje Kotlina.",
        onApplyClick = {},
        isApplying = false,
        isApplied = false
    )
}