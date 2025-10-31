package hr.wortex.otpstudent.ui.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hr.wortex.otpstudent.R
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.usecase.Login

@Composable
fun LoginScreen(paddingValues: PaddingValues, navController: NavController) {
    val context = LocalContext.current

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(DependencyProvider.login)
    )

    val uiState by loginViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> navController.navigate("home_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
            is LoginUiState.Error -> Toast.makeText(
                context,
                (uiState as LoginUiState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF056f52)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                painter = painterResource(id = R.drawable.otp),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row {
                Text(
                    text = "OTP",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Student",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFf2701b)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(if (emailError.isNotEmpty()) emailError else "Email", color = if (emailError.isNotEmpty()) Color.Red else Color.Unspecified) },
                leadingIcon = { Icon(Icons.Rounded.AccountCircle, contentDescription = "") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if (passwordError.isNotEmpty()) passwordError else "Lozinka", color = if (passwordError.isNotEmpty()) Color.Red else Color.Unspecified) },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    emailError = if (email.isBlank()) "Potrebno je unijeti Email adresu" else ""
                    passwordError = if (password.isBlank()) "Potrebno je unijeti lozinku" else ""

                    if (emailError.isEmpty() && passwordError.isEmpty()) {
                        loginViewModel.loginUser(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 90.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
            ) {
                Text(text = "Prijava")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zaboravljena lozinka?",
                color = Color.White,
                modifier = Modifier.clickable { /* TODO: handle forgot password */ }
            )

            Spacer(modifier = Modifier.height(50.dp))

            Row {
                Text(text = "Nemaš račun? ", color = Color(0xFFf2701b))
                Text(
                    text = "Registriraj se!",
                    color = Color.White,
                    modifier = Modifier.clickable { /* TODO: handle registration */ }
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LoginScreen(paddingValues = innerPadding, navController = navController)
    }
}
