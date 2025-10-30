package hr.wortex.otpstudent.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.painterResource
import hr.wortex.otpstudent.R

@Composable
fun LoginScreen(paddingValues: PaddingValues) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(color = 0xFF056f52)
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

            Text(text = "Prijava", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        emailError.ifEmpty { "Email" },
                        color = if (emailError.isNotEmpty()) Color.Red else Color.Unspecified
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.AccountCircle,
                        contentDescription = ""
                    )
                },
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
                label = {
                    Text(
                        passwordError.ifEmpty { "Lozinka" },
                        color = if (passwordError.isNotEmpty()) Color.Red else Color.Unspecified
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Lock,
                        contentDescription = ""
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    //aa
                },
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
                        //login logika
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 90.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
            ) {
                Text(
                    text = "Prijava"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zaboravljena lozinka?",
                color = Color.White,
                modifier = Modifier.clickable {
                    //rjesit zaboravljenu lozinku
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            Row {
                Text(
                    text = "Nemaš račun? ",
                    color = Color(0xFFf2701b)
                )

                Text(
                    text = "Registriraj se!",
                    color = Color.White,
                    modifier = Modifier.clickable {
                        // logika registracije
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LoginScreen(paddingValues = innerPadding)
    }
}