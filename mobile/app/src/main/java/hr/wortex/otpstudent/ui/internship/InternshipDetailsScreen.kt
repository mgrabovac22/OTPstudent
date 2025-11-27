package hr.wortex.otpstudent.ui.internship

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipDetailsScreen(
    navController: NavController,
    viewModel: InternshipApplicationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    val weeksOptions = (2..8).toList()

    LaunchedEffect(uiState.submissionState) {
        if (uiState.submissionState is SubmissionState.Success) {
            delay(500)
            navController.navigate("home_screen") {
                popUpTo("internship_application_graph") { inclusive = true }
            }
        }
    }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            viewModel.updateStartDate(calendar.timeInMillis)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

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
                onClick = { viewModel.submitApplication() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
            ) {
                when (uiState.submissionState) {
                    is SubmissionState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    }
                    is SubmissionState.Success -> {
                        Text("Prijava uspješna!")
                    }
                    else -> {
                        Text("Potvrdi prijavu")
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Trajanje prakse:", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = "${uiState.practiceDurationInWeeks} tjedana",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    weeksOptions.forEach { week ->
                        DropdownMenuItem(
                            text = { Text("$week tjedana") },
                            onClick = {
                                viewModel.updateDuration(week)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text("Kada bi Vam najviše odgovaralo provođenje prakse?", style = MaterialTheme.typography.titleMedium)

            Box {
                val dateValue = when {
                    uiState.expectedEndDate.isNotEmpty() -> "${uiState.expectedStartDate} do ${uiState.expectedEndDate}"
                    uiState.expectedStartDate.isNotEmpty() -> uiState.expectedStartDate
                    else -> ""
                }
                OutlinedTextField(
                    value = dateValue,
                    onValueChange = {},
                    label = { Text("Odaberite željeni datum početka prakse") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePicker.show() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Što očekujete od prakse u OTP banci?", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = uiState.practiceExpectations,
                onValueChange = { viewModel.updateExpectations(it) },
                modifier = Modifier.fillMaxWidth().weight(1f),
                placeholder = { Text("Unesite svoja očekivanja...") }
            )
            
            uiState.detailsError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}