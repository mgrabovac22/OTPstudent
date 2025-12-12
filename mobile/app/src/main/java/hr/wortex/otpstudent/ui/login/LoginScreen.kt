package hr.wortex.otpstudent.ui.login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
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

@Composable
fun LoginScreen(paddingValues: PaddingValues, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(DependencyProvider.login))
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf<String?>(null) }

    val AppDarkGreen = Color(0xFF056f52)
    val AppOrange = Color(0xFFf2701b)
    val InputBorderColor = Color(0xFFCCCCCC)
    val InputTextColor = Color.Black

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                navController.navigate("home_screen") {
                    popUpTo("login_screen") { inclusive = true }
                }
            }
            is LoginUiState.Error -> {
                generalError = (uiState as LoginUiState.Error).message
            }
            else -> {
                generalError = null
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppDarkGreen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.otp),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text("OTP", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text("Student", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = AppOrange)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text(emailError ?: "Email", color = if (emailError != null) Color.Red else Color.Gray) },
                leadingIcon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
                isError = emailError != null,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,

                    focusedTextColor = InputTextColor,
                    unfocusedTextColor = InputTextColor,

                    focusedIndicatorColor = AppDarkGreen,
                    focusedLeadingIconColor = AppDarkGreen,
                    focusedLabelColor = AppDarkGreen,
                    cursorColor = AppDarkGreen,

                    unfocusedIndicatorColor = InputBorderColor,
                    unfocusedLeadingIconColor = AppDarkGreen.copy(alpha = 0.6f),
                    unfocusedLabelColor = Color.Gray,

                    errorIndicatorColor = Color.Red,
                    errorLeadingIconColor = Color.Red,
                    errorLabelColor = Color.Red
                )
            )

            if (emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text(passwordError ?: "Lozinka", color = if (passwordError != null) Color.Red else Color.Gray) },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,

                    focusedTextColor = InputTextColor,
                    unfocusedTextColor = InputTextColor,

                    focusedIndicatorColor = AppDarkGreen,
                    focusedLeadingIconColor = AppDarkGreen,
                    focusedLabelColor = AppDarkGreen,
                    cursorColor = AppDarkGreen,

                    unfocusedIndicatorColor = InputBorderColor,
                    unfocusedLeadingIconColor = AppDarkGreen.copy(alpha = 0.6f),
                    unfocusedLabelColor = Color.Gray,

                    errorIndicatorColor = Color.Red,
                    errorLeadingIconColor = Color.Red,
                    errorLabelColor = Color.Red
                )
            )

            if (passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    emailError = viewModel.validateEmail(email)
                    passwordError = viewModel.validatePassword(password)

                    if (emailError == null && passwordError == null) {
                        viewModel.loginUser(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 90.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppOrange)
            ) {
                if (uiState == LoginUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Text(text = "Prijava")
                }
            }

            if (generalError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = generalError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zaboravljena lozinka?",
                color = Color.White,
                modifier = Modifier.clickable { }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row {
                Text("Nemaš račun? ", color = AppOrange)
                Text(
                    "Registriraj se!",
                    color = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate("registration_screen")
                    }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginPreview() {
    val navController = rememberNavController()
    Scaffold { padding ->
        LoginScreen(paddingValues = padding, navController = navController)
    }
}