package hr.wortex.otpstudent.ui.internship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipUserDataScreen(
    navController: NavController,
    viewModel: InternshipApplicationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    fun formatDisplayDate(apiDate: String?): String {
        if (apiDate == null) return ""
        return try {
            val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = apiFormat.parse(apiDate)
            val displayFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
            date?.let { displayFormat.format(it) } ?: ""
        } catch (e: Exception) {
            apiDate
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prijava za praksu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (viewModel.validateUserDataStep()) {
                        navController.navigate("internship_jobs_screen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
            ) {
                Text("Dalje")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (user != null) {
                Text(
                    text = "Molimo Vas da provjerite točnost upisanih podataka i u prazna polja upišete tražene podatke.",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = "${user.firstName} ${user.lastName}",
                    onValueChange = {},
                    label = { Text("Ime i Prezime") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = formatDisplayDate(user.dateOfBirth),
                    onValueChange = {},
                    label = { Text("Datum Rođenja") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = "Fakultet organizacije i informatike",
                    onValueChange = {},
                    label = { Text("Naziv Fakulteta") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = user.areaOfStudy ?: "",
                    onValueChange = {},
                    label = { Text("Smjer") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = user.yearOfStudy?.toString() ?: "",
                    onValueChange = {},
                    label = { Text("Godina pohađanja") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.studentAddress,
                    onValueChange = { newValue -> viewModel.updateAddress(newValue) },
                    label = { Text("Adresa") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.addressError != null,
                    readOnly = false
                )
                uiState.addressError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.contactNumber,
                    onValueChange = { newValue -> viewModel.updateContactNumber(newValue) },
                    label = { Text("Kontakt broj") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.contactNumberError != null,
                    readOnly = false
                )
                uiState.contactNumberError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = user.email,
                    onValueChange = {},
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                
            } else {
                Text("Učitavanje podataka...")
            }
        }
    }
}
