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

    val AppGreen = Color(0xFF1B6E2A)
    val AppOrange = Color(0xFFf2701b)
    val White = Color.White
    val Black = Color.Black
    val RedError = MaterialTheme.colorScheme.error

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

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AppGreen,
        unfocusedBorderColor = AppGreen,
        focusedLabelColor = AppGreen,
        unfocusedLabelColor = AppGreen,
        focusedTextColor = Black,
        unfocusedTextColor = Black,
        cursorColor = AppGreen,
        errorBorderColor = AppGreen,
        errorLabelColor = AppGreen,
        errorTextColor = Black,
        errorCursorColor = AppGreen,
        focusedTrailingIconColor = AppGreen,
        unfocusedTrailingIconColor = AppGreen,
        errorTrailingIconColor = AppGreen
    )

    val primaryTextColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prijava za praksu", fontWeight = FontWeight.Bold, color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppGreen)
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.submitApplication() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppOrange, contentColor = White),
                enabled = uiState.submissionState !is SubmissionState.Loading
            ) {
                when (uiState.submissionState) {
                    is SubmissionState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
                    }
                    is SubmissionState.Success -> {
                        Text("Prijava uspješna!")
                    }
                    else -> {
                        Text("Potvrdi prijavu")
                    }
                }
            }
        },
        containerColor = White
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

            Text("Trajanje prakse:", style = MaterialTheme.typography.titleMedium, color = AppGreen)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = "${uiState.practiceDurationInWeeks} tjedana",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    weeksOptions.forEach { week ->
                        DropdownMenuItem(
                            text = { Text("$week tjedana", color = Black) },
                            onClick = {
                                viewModel.updateDuration(week)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = primaryTextColor,
                            )
                        )
                    }
                }
            }

            Text("Kada bi Vam najviše odgovaralo provođenje prakse?", style = MaterialTheme.typography.titleMedium, color = AppGreen)

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
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePicker.show() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Što očekujete od prakse u OTP banci?", style = MaterialTheme.typography.titleMedium, color = AppGreen)
            OutlinedTextField(
                value = uiState.practiceExpectations,
                onValueChange = { viewModel.updateExpectations(it) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("Unesite svoja očekivanja...", color = AppGreen.copy(alpha = 0.5f)) },
                colors = textFieldColors,
                isError = uiState.detailsError != null
            )

            uiState.detailsError?.let {
                Text(text = it, color = RedError, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}