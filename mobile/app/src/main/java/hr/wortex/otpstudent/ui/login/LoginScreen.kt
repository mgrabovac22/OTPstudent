package hr.wortex.otpstudent.ui.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch

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

    var canUseBiometric by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val hasTokens = DependencyProvider.authRepository.hasSavedTokens()
        canUseBiometric = hasTokens && BiometricHelper.isAvailable(context)
    }

    var generalError by remember { mutableStateOf<String?>(null) }

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
        color = Color(0xFF056f52)
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
                Text("Student", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFf2701b))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Email
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text(emailError ?: "Email", color = if (emailError != null) Color.Red else Color.Unspecified) },
                leadingIcon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
                isError = emailError != null,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.White
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

            // Password
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text(passwordError ?: "Lozinka", color = if (passwordError != null) Color.Red else Color.Unspecified) },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.White
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
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

            if (canUseBiometric) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            BiometricHelper.prompt(
                                activity = activity,
                                onSuccess = {
                                    scope.launch {
                                        try {
                                            DependencyProvider.userRepository.getCurrentUser()
                                            navController.navigate("home_screen") {
                                                popUpTo("login_screen") { inclusive = true }
                                            }
                                        } catch (e: Exception) {
                                            generalError = "Sesija istekla. Prijavite se lozinkom."
                                        }
                                    }
                                },
                                onError = { msg -> generalError = msg }
                            )
                        } else {
                            generalError = "Biometrija nije podržana u ovoj aktivnosti"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 90.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
                ) {
                    Text(text = "Prijava otiskom prsta")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zaboravljena lozinka?",
                color = Color.White,
                modifier = Modifier.clickable { /* TODO: handle forgot password */ }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row {
                Text("Nemaš račun? ", color = Color(0xFFf2701b))
                Text(
                    "Registriraj se!",
                    color = Color.White,
                    modifier = Modifier.clickable { /* TODO: handle registration */ }
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