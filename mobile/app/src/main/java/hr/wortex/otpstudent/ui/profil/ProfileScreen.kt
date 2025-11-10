package hr.wortex.otpstudent.ui.profil

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.model.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onEditProfile: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = viewModel { ProfileViewModel(DependencyProvider.userRepository) }
    val uiState by viewModel.uiState.collectAsState()

    var selectedFileName by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val context = LocalContext.current

    // File picker launcher (PDF)
    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Custom(listOf("application/pdf")),
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            if (files.isNotEmpty()) {
                val uri = files.first().uri
                selectedFileName = uri.getDisplayName(context) ?: ProfileStrings.DefaultPdfName
            } else {
                messageText = ProfileStrings.FileNotSelected
            }
        }
    )

    Scaffold(
        topBar = { ProfileTopBar(onEditProfile) }
    ) { paddingValues ->
        when (uiState) {
            is ProfileUiState.Loading -> ProfileLoadingScreen(paddingValues)
            is ProfileUiState.Error -> ProfileErrorScreen((uiState as ProfileUiState.Error).message, paddingValues)
            is ProfileUiState.Success -> {
                val user = (uiState as ProfileUiState.Success).user
                ProfileContent(
                    user = user,
                    selectedPdfName = selectedFileName,
                    onUploadClick = {
                        try {
                            pickerLauncher.launch()
                        } catch (e: Exception) {
                            selectedFileName = ProfileStrings.EmulatorDummyFile
                            messageText = ProfileStrings.EmulatorPickerMessage
                        }
                    },
                    paddingValues = paddingValues,
                    messageText = messageText,
                    onMessageChange = { messageText = it }
                )
            }
        }
    }
}

// ----------------------------
//  UI SUBCOMPONENTS
// ----------------------------
@Composable
private fun ProfileLoadingScreen(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
private fun ProfileErrorScreen(message: String, padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${ProfileStrings.ErrorPrefix} $message",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(onEditProfile: () -> Unit) {
    TopAppBar(
        title = { ProfileTopBarTitle() },
        actions = {
            IconButton(onClick = onEditProfile) {
                Icon(Icons.Default.Edit, contentDescription = ProfileStrings.Edit)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun ProfileTopBarTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ProfileStrings.AppNameOtp,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileColors.LogoTeal
        )
        Text(
            text = ProfileStrings.AppNameStudent,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileColors.LogoOrange
        )
    }
}

@Composable
private fun ProfileContent(
    user: UserProfile,
    selectedPdfName: String,
    onUploadClick: () -> Unit,
    paddingValues: PaddingValues,
    messageText: String,
    onMessageChange: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(Dimens.PaddingLarge)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(Dimens.PaddingExtraLarge))

        // Avatar
        Box(
            modifier = Modifier
                .size(Dimens.AvatarSize)
                .clip(CircleShape)
                .background(ProfileColors.AvatarBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = user.imagePath.toString(), fontSize = 48.sp)
        }

        Spacer(Modifier.height(Dimens.PaddingLarge))

        Text(
            text = "${user.firstName} ${user.lastName}",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(Modifier.height(Dimens.PaddingExtraLarge))

        // Info cards
        ProfileInfoCard(user.email)
        Spacer(Modifier.height(Dimens.PaddingMedium))
        ProfileInfoCard(ProfileStrings.FacultyName)
        Spacer(Modifier.height(Dimens.PaddingMedium))
        ProfileInfoCard(user.yearOfStudy?.let { ProfileStrings.yearOfStudyFormatted(it) } ?: ProfileStrings.YearNotSet)
        Spacer(Modifier.height(Dimens.PaddingMedium))
        ProfileInfoCard(user.areaOfStudy ?: ProfileStrings.AreaNotSet)

        Spacer(Modifier.height(Dimens.PaddingMedium))

        // CV Upload
        UploadCvCard(selectedPdfName, onUploadClick)

        Spacer(Modifier.height(Dimens.PaddingMedium))

        // Upload button
        if (selectedPdfName.isNotEmpty()) {
            Button(
                onClick = {
                    scope.launch {
                        onMessageChange(ProfileStrings.UploadSuccess)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingLarge),
                colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.ButtonBackground),
                shape = RoundedCornerShape(Dimens.PaddingLarge)
            ) {
                Text(
                    text = ProfileStrings.UploadCv,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            Spacer(Modifier.height(Dimens.PaddingSmall))
        }

        // Poruka ispod forme
        if (messageText.isNotEmpty()) {
            Text(
                text = messageText,
                color = if (messageText == ProfileStrings.UploadSuccess) Color.Green else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingLarge)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun UploadCvCard(selectedPdfName: String, onUploadClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(Dimens.CardHeight),
        shape = RoundedCornerShape(Dimens.PaddingSmall),
        colors = CardDefaults.cardColors(containerColor = ProfileColors.CardBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedPdfName.isEmpty()) ProfileStrings.UploadCv else selectedPdfName,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            IconButton(onClick = onUploadClick) {
                Icon(Icons.Default.Edit, contentDescription = ProfileStrings.SelectCv, tint = Color.Gray)
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(Dimens.CardHeight),
        shape = RoundedCornerShape(Dimens.PaddingSmall),
        colors = CardDefaults.cardColors(containerColor = ProfileColors.CardBackground)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.PaddingLarge),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = text, fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

// ----------------------------
//  HELPERS
// ----------------------------
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


// Privatni objekti za konstante, da se izbjegnu "magic numbers" i hardkodirani stringovi
// ovo se moe staviti i u color.kt, theme.kt, type.kt

public object ProfileColors {
    val LogoTeal = Color(0xFF006B5C)
    val LogoOrange = Color(0xFFFF8C42)
    val AvatarBackground = Color(0xFFF4A5B9)
    val CardBackground = Color(0xFFE8E8E8)
    val ButtonBackground = Color(0xFFf2701b)
}

public object Dimens {
    val PaddingSmall = 8.dp
    val PaddingMedium = 12.dp
    val PaddingLarge = 16.dp
    val PaddingExtraLarge = 24.dp
    val AvatarSize = 120.dp
    val CardHeight = 56.dp
}

public object ProfileStrings {
    const val AppNameOtp = "OTP"
    const val AppNameStudent = "Student"
    const val Back = "Natrag"
    const val Edit = "Uredi"
    const val ErrorPrefix = "Greška: "
    const val DefaultPdfName = "odabrano_file.pdf"
    const val FileNotSelected = "Nije odabrana nijedna datoteka"
    const val UploadCv = "Upload CV"
    const val SelectCv = "Odaberi CV"
    const val UploadSuccess = "CV uspješno učitan"
    const val FacultyName = "Fakultet organizacije i informatike"
    const val YearNotSet = "Godina nije upisana"
    const val AreaNotSet = ""
    const val EmulatorPickerMessage =
        "Picker nije dostupan na emulatoru. Odabrana dummy datoteka."
    const val EmulatorDummyFile = "dummy.pdf"

    fun yearOfStudyFormatted(year: Int) = "$year. godina"
}