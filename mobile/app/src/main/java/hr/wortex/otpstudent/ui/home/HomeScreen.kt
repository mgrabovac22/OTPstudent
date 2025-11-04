package hr.wortex.otpstudent.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.wortex.otpstudent.R
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.usecase.GetUser

@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val getUserUseCase = remember { GetUser(DependencyProvider.userRepository) }
    val viewModel = remember { HomeViewModel(getUserUseCase) }
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
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
                    verticalArrangement = Arrangement.Top
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
                                style = MaterialTheme.typography.headlineSmall,
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
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(50.dp, 50.dp, 50.dp, 0.dp).fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    // TODO: implement logic
                                },
                                modifier = Modifier.padding(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b)),
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

                            Spacer(Modifier.height(30.dp))

                            Button(
                                onClick = {
                                    // TODO: implement logic
                                },
                                modifier = Modifier.padding(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b)),
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

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview(){
    Scaffold { paddingValues ->
        HomeScreen(paddingValues)
    }
}
