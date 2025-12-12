package hr.wortex.otpstudent.ui.editProfile

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.ui.profil.edit.DateUtils
import hr.wortex.otpstudent.ui.profil.edit.EditProfileState
import hr.wortex.otpstudent.ui.profil.edit.EditProfileViewModel
import java.util.Calendar

private val AppGreen = Color(0xFF1B6E2A)
private val AppOrange = Color(0xFFf2701b)
private val White = Color.White
private val Black = Color.Black

private object ProfileColors {
    val LogoTeal = Color(0xFF006B5C)
    val LogoOrange = Color(0xFFFF8C42)
    val AvatarBackground = Color(0xFFF4A5B9)
    val CardBackground = Color(0xFFE8E8E8)
    val ButtonBackground = AppOrange
}

private object Dimens {
    val PaddingSmall = 8.dp
    val PaddingMedium = 12.dp
    val PaddingLarge = 16.dp
    val PaddingExtraLarge = 24.dp
    val AvatarSize = 120.dp
    val CardHeight = 56.dp
}


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
                title = { Text("Uredi profil", fontWeight = FontWeight.Bold, color = White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppGreen)
            )
        },
        containerColor = White
    ) { scaffoldPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AppGreen)
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
                        .background(White.copy(alpha = 0.9f))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    state: EditProfileState,
    viewModel: EditProfileViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            if (files.isNotEmpty()) {
                val uri = files.first().uri

                val fileName = uri.getDisplayName(context) ?: "image.jpg"
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

                val inputStream = context.contentResolver.openInputStream(uri)

                if (inputStream != null) {
                    viewModel.uploadImage(inputStream, fileName, mimeType)
                }
            }
        }
    )

    val latestAllowed = remember { DateUtils.latestAllowedBirthDateMillis() }

    val showDatePicker = remember(state.dateOfBirth, latestAllowed) {
        {
            val initialMillis = DateUtils.displayToMillis(state.dateOfBirth)?.let {
                if (it <= latestAllowed) it else latestAllowed
            } ?: latestAllowed

            val cal = Calendar.getInstance().apply { timeInMillis = initialMillis }

            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    viewModel.onDateSelected(selectedCal.timeInMillis)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = latestAllowed
                datePicker.minDate = Calendar.getInstance().apply { set(1900, Calendar.JANUARY, 1) }.timeInMillis
            }.show()
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
                .background(Color.LightGray)
                .clickable {
                    imagePickerLauncher.launch()
                },
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
                Text("?", fontSize = 48.sp, color = Black)
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Promijeni sliku",
                    tint = White,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Black.copy(alpha = 0.5f), CircleShape)
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.imageError != null) {
            Text(
                text = state.imageError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Text("Klikni na sliku za promjenu", fontSize = 12.sp, color = AppGreen)

        Spacer(Modifier.height(Dimens.PaddingExtraLarge))

        EditProfileTextField(
            label = "Email",
            value = state.email,
            onValueChange = {},
            readOnly = true,
            error = state.emailError,
            colorConfig = TextFieldColorConfig(AppGreen, Black)
        )

        EditProfileTextField(
            label = "Ime",
            value = state.firstName,
            onValueChange = viewModel::onFirstNameChange,
            error = state.firstNameError,
            colorConfig = TextFieldColorConfig(AppGreen, Black)
        )

        EditProfileTextField(
            label = "Prezime",
            value = state.lastName,
            onValueChange = viewModel::onLastNameChange,
            error = state.lastNameError,
            colorConfig = TextFieldColorConfig(AppGreen, Black)
        )

        EditProfileTextField(
            label = "Godina studija",
            value = state.yearOfStudy,
            onValueChange = viewModel::onYearOfStudyChange,
            keyboardType = KeyboardType.Number,
            error = state.yearOfStudyError,
            colorConfig = TextFieldColorConfig(AppGreen, Black)
        )

        EditProfileTextField(
            label = "Smjer studija",
            value = state.areaOfStudy,
            onValueChange = viewModel::onAreaOfStudyChange,
            error = state.areaOfStudyError,
            colorConfig = TextFieldColorConfig(AppGreen, Black)
        )

        OutlinedTextField(
            value = state.dateOfBirth,
            onValueChange = { },
            label = { Text("Datum rođenja") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Odaberi datum", tint = AppGreen)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { showDatePicker() },
            enabled = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppGreen,
                focusedLabelColor = AppGreen,
                cursorColor = AppGreen,
                unfocusedBorderColor = AppGreen,
                unfocusedLabelColor = AppGreen,

                focusedTextColor = Black,
                unfocusedTextColor = Black,

                disabledTextColor = Black,
                disabledLabelColor = AppGreen,
                disabledBorderColor = AppGreen
            )
        )

        Spacer(Modifier.height(Dimens.PaddingExtraLarge))

        Button(
            onClick = { viewModel.saveProfile() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppOrange),
            shape = RoundedCornerShape(Dimens.PaddingLarge)
        ) {
            Text(
                text = "Spremi promjene",
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Text(
            text = "Promijeni lozinku",
            color = AppGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = Dimens.PaddingLarge)
                .clickable { viewModel.showPasswordDialog() }
        )

        Spacer(Modifier.height(32.dp))
    }

    if (state.isChangePasswordModalVisible) {
        ChangePasswordDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.hidePasswordDialog() }
        )
    }
}

data class TextFieldColorConfig(
    val focusedPrimary: Color,
    val unfocusedPrimary: Color
)

@Composable
fun EditProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    error: String? = null,
    colorConfig: TextFieldColorConfig
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorConfig.focusedPrimary,
                focusedLabelColor = colorConfig.focusedPrimary,
                cursorColor = colorConfig.focusedPrimary,
                unfocusedBorderColor = colorConfig.focusedPrimary,
                unfocusedLabelColor = colorConfig.focusedPrimary,
                focusedTextColor = colorConfig.unfocusedPrimary,
                unfocusedTextColor = colorConfig.unfocusedPrimary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

private fun Uri.getDisplayName(context: Context): String? {
    return try {
        context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else null
        }
    } catch (e: Exception) {
        null
    }
}