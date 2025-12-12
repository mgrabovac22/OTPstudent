package hr.wortex.otpstudent.ui.sustav_nagardivanja.informativni_sadrzaj

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.wortex.otpstudent.di.DependencyProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDetailScreen(
    contentId: Int,
    navController: NavController
) {
    val getDetailUseCase = remember { DependencyProvider.getInfoContentDetailUseCase }
    val markReadUseCase = remember { DependencyProvider.markInfoContentReadUseCase }
    val getUserUseCase = remember { DependencyProvider.getUserUseCase }

    val viewModel = remember {
        InfoDetailViewModel(getDetailUseCase, markReadUseCase, getUserUseCase)
    }

    LaunchedEffect(contentId) {
        viewModel.fetchInfoDetail(contentId)
    }

    val uiState by viewModel.uiState.collectAsState()

    val AppGreen = Color(0xFF1B6E2A)
    val AppOrange = Color(0xFFf2701b)
    val White = Color.White
    val Black = Color.Black
    val RedError = MaterialTheme.colorScheme.error

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalji", fontWeight = FontWeight.Bold, color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppGreen)
            )
        },
        containerColor = White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is InfoDetailUiState.Loading -> {
                    CircularProgressIndicator(color = AppGreen)
                }
                is InfoDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = RedError,
                        textAlign = TextAlign.Center
                    )
                }
                is InfoDetailUiState.Success -> {
                    val content = state.infoContent

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = content.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = AppGreen,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = content.description,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Justify,
                            color = Black,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        Button(
                            onClick = {
                                viewModel.onFinishClick(contentId) {
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppOrange,
                                contentColor = White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Završi +${content.experiencePoints}xp",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}