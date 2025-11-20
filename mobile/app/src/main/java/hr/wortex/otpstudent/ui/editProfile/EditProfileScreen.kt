package hr.wortex.otpstudent.ui.editProfile

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.ui.profil.Dimens
import hr.wortex.otpstudent.ui.profil.ProfileColors
import hr.wortex.otpstudent.ui.profil.edit.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    padding: PaddingValues,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: EditProfileViewModel = viewModel {
        EditProfileViewModel(DependencyProvider.userRepository)
    }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            viewModel.onSavedHandled()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Uredi profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { scaffoldPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                EditProfileContent(
                    state = state,
                    viewModel = viewModel
                )
            }

            state.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.9f))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    state: hr.wortex.otpstudent.ui.profil.edit.EditProfileState,
    viewModel: EditProfileViewModel
) {
    val scrollState = rememberScrollState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) {
                    Text("Odaberi", color = ProfileColors.LogoTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Otkaži", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingLarge)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(Dimens.PaddingExtraLarge))

        Box(
            modifier = Modifier
                .size(Dimens.AvatarSize)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            val imageBitmap: ImageBitmap? = remember(state.imageBase64) {
                state.imageBase64?.let { base64String ->
                    try {
                        val pureBase64 = base64String.substringAfter(",")
                        val bytes = android.util.Base64.decode(pureBase64, android.util.Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            if (imageBitmap != null) {
                Image(
                    painter = BitmapPainter(imageBitmap),
                    contentDescription = "Profile image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("?", fontSize = 48.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("(Slika se ne može mijenjati ovdje)", fontSize = 12.sp, color = Color.Gray)

        Spacer(Modifier.height(Dimens.PaddingExtraLarge))


        EditProfileTextField(
            label = "Email",
            value = state.email,
            onValueChange = {},
            readOnly = true
        )

        EditProfileTextField(
            label = "Ime",
            value = state.firstName,
            onValueChange = viewModel::onFirstNameChange
        )

        EditProfileTextField(
            label = "Prezime",
            value = state.lastName,
            onValueChange = viewModel::onLastNameChange
        )

        EditProfileTextField(
            label = "Godina studija",
            value = state.yearOfStudy,
            onValueChange = viewModel::onYearOfStudyChange,
            keyboardType = KeyboardType.Number
        )

        EditProfileTextField(
            label = "Smjer studija",
            value = state.areaOfStudy,
            onValueChange = viewModel::onAreaOfStudyChange
        )

        OutlinedTextField(
            value = state.dateOfBirth,
            onValueChange = { },
            label = { Text("Datum rođenja") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Odaberi datum")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { showDatePicker = true },
            enabled = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProfileColors.LogoTeal,
                focusedLabelColor = ProfileColors.LogoTeal,
                cursorColor = ProfileColors.LogoTeal,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledBorderColor = Color.Gray
            )
        )

        Spacer(Modifier.height(Dimens.PaddingExtraLarge))

        Button(
            onClick = { viewModel.saveProfile() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.ButtonBackground),
            shape = RoundedCornerShape(Dimens.PaddingLarge)
        ) {
            Text(
                text = "Spremi promjene",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun EditProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProfileColors.LogoTeal,
                focusedLabelColor = ProfileColors.LogoTeal,
                cursorColor = ProfileColors.LogoTeal
            )
        )
    }
}