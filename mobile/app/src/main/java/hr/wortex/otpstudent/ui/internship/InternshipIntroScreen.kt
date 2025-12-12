package hr.wortex.otpstudent.ui.internship

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipIntroScreen(navController: NavController) {
    val AppGreen = Color(0xFF1B6E2A)
    val AppOrange = Color(0xFFf2701b)
    val TextColor = AppGreen
    val ButtonColor = AppOrange
    val TopBarContentColor = Color.White
    val TopBarBackground = AppGreen
    val ScreenBackground = Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Studentska praksa", fontWeight = FontWeight.Bold, color = TopBarContentColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag", tint = TopBarContentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarBackground)
            )
        },
        containerColor = ScreenBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Iskusi rad u našem timu kroz stručnu praksu i stekni vrijedno iskustvo u dinamičnom okruženju OTP banke!",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = TextColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ako si zainteresiran/a, popuni Prijavni upitnik u prilogu.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = TextColor
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("internship_user_data_screen") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor,
                    contentColor = Color.White
                )
            ) {
                Text("Prijavni upitnik za obavljanje stručne prakse")
            }
        }
    }
}