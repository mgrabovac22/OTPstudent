package hr.wortex.otpstudent.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.wortex.otpstudent.R
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.usecase.GetUser

private object HomeColors {
    val LogoTeal = Color(0xFF006B5C)
    val LogoOrange = Color(0xFFFF8C42)
    val TopBarGreen = Color(0xFF1B6E2A)
}

private object HomeStrings {
    const val AppNameOtp = "OTP"
    const val AppNameStudent = "Student"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(paddingValues: PaddingValues, onRewardClick: () -> Unit = {}) {
    val getUserUseCase = remember { GetUser(DependencyProvider.userRepository) }
    val viewModel = remember { HomeViewModel(getUserUseCase) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { HomeTopBarTitle() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HomeColors.TopBarGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = Color.White
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(0.dp),
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
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Column (
                                modifier = Modifier
                                    .padding(20.dp,20.dp,20.dp,0.dp)
                                    .fillMaxWidth()
                            ){

                                Text(
                                    text = "Pozdrav ${user.firstName}! 👋",
                                    color = HomeColors.LogoOrange,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Financije za 5 - Postanite odlikaš u upravljanju svojim novcem",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )

                                Image(
                                    painter = painterResource(R.drawable.home_screen),
                                    contentDescription = "Home screen image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 20.dp, 0.dp, 0.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.padding(30.dp,10.dp).fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onRewardClick() },
                                    modifier = Modifier.padding(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b), contentColor = Color.White),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(text = "OTPakiraj svoju karijeru",
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Button(
                                    onClick = {
                                        // TODO: implement logic
                                    },
                                    modifier = Modifier.padding(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b), contentColor = Color.White),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(text = "OTP Frend",
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
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
}

@Composable
private fun HomeTopBarTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = HomeStrings.AppNameOtp,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = HomeStrings.AppNameStudent,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = HomeColors.LogoOrange
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(PaddingValues(0.dp))
}