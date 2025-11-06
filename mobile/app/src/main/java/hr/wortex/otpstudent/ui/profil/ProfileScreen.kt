package hr.wortex.otpstudent.ui.profil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.model.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onEditProfile: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = viewModel {
        ProfileViewModel(DependencyProvider.userRepository)
    }
    val uiState by viewModel.uiState.collectAsState()
    var selectedFileName by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val name = uri.lastPathSegment?.substringAfterLast("/")
                    ?: ProfileStrings.DefaultPdfName
                selectedFileName = name
                messageText = "${ProfileStrings.FileSelectedPrefix} $name"
            } catch (e: Exception) {
                messageText = ProfileStrings.FileReadError
            }
        } else {
            messageText = ProfileStrings.FileNotSelected
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(
                onNavigateBack = onNavigateBack,
                onEditProfile = onEditProfile
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${ProfileStrings.ErrorPrefix} ${(uiState as ProfileUiState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            is ProfileUiState.Success -> {
                val user = (uiState as ProfileUiState.Success).user
                ProfileContent(
                    user = user,
                    selectedPdfName = selectedFileName,
                    onUploadClick = {
                        try {
                            launcher.launch("application/pdf")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit
) {
    TopAppBar(
        title = { ProfileTopBarTitle() },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = ProfileStrings.Back
                )
            }
        },
        actions = {
            IconButton(onClick = onEditProfile) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = ProfileStrings.Edit
                )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

        // Avatar
        Box(
            modifier = Modifier
                .size(Dimens.AvatarSize)
                .clip(CircleShape)
                .background(ProfileColors.AvatarBackground),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Učitati sliku sa servera
            Text(text = user.imagePath.toString(), fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

        // Ime
        Text(
            text = "${user.firstName} ${user.lastName}",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

        // Info kartice
        ProfileInfoCard(text = user.email)
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        ProfileInfoCard(text = ProfileStrings.FacultyName)
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        ProfileInfoCard(
            text = user.yearOfStudy?.let { ProfileStrings.yearOfStudyFormatted(it) }
                ?: ProfileStrings.YearNotSet
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        ProfileInfoCard(text = user.areaOfStudy ?: ProfileStrings.AreaNotSet)
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

        // Odabir CV
        UploadCvCard(
            selectedPdfName = selectedPdfName,
            onUploadClick = onUploadClick
        )

        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

        // Pravi Upload gumb, vidi se samo kad je PDF odabran
        if (selectedPdfName.isNotEmpty()) {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            // TODO: zamijeni sa stvarnom funkcijom koja upload-a na server
                            // viewModel.uploadCv(file)
                            onMessageChange(ProfileStrings.UploadSuccess)
                        } catch (e: Exception) {
                            onMessageChange(ProfileStrings.UploadError)
                        }
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

            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
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
    }
}

@Composable
private fun UploadCvCard(
    selectedPdfName: String,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.CardHeight),
        shape = RoundedCornerShape(Dimens.PaddingSmall),
        colors = CardDefaults.cardColors(containerColor = ProfileColors.CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedPdfName.isEmpty()) ProfileStrings.UploadCv else selectedPdfName,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            IconButton(onClick = onUploadClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = ProfileStrings.SelectCv,
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ProfileInfoCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.CardHeight),
        shape = RoundedCornerShape(Dimens.PaddingSmall),
        colors = CardDefaults.cardColors(containerColor = ProfileColors.CardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.PaddingLarge),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}

// Privatni objekti za konstante, da se izbjegnu "magic numbers" i hardkodirani stringovi

private object ProfileColors {
    val LogoTeal = Color(0xFF006B5C)
    val LogoOrange = Color(0xFFFF8C42)
    val AvatarBackground = Color(0xFFF4A5B9)
    val CardBackground = Color(0xFFE8E8E8)
    val ButtonBackground = Color(0xFFf2701b)
}

private object Dimens {
    val PaddingSmall = 8.dp
    val PaddingMedium = 12.dp
    val PaddingLarge = 16.dp
    val PaddingExtraLarge = 24.dp
    val AvatarSize = 120.dp
    val CardHeight = 56.dp
}

private object ProfileStrings {
    const val AppNameOtp = "OTP"
    const val AppNameStudent = "Student"
    const val Back = "Natrag"
    const val Edit = "Uredi"
    const val ErrorPrefix = "Greška: "
    const val DefaultPdfName = "odabrano_file.pdf"
    const val FileSelectedPrefix = "Odabrano:"
    const val FileReadError = "Ne mogu pročitati datoteku"
    const val FileNotSelected = "Nije odabrana nijedna datoteka"
    const val UploadCv = "Upload CV"
    const val SelectCv = "Odaberi CV"
    const val UploadSuccess = "CV uspješno učitan"
    const val UploadError = "Greška pri učitavanju CV-a"
    const val FacultyName = "Fakultet organizacije i informatike"
    const val YearNotSet = "Godina nije upisana"
    const val AreaNotSet = ""
    const val EmulatorPickerMessage =
        "Picker nije dostupan na emulatoru. Odabrana dummy datoteka."
    const val EmulatorDummyFile = "dummy.pdf"

    fun yearOfStudyFormatted(year: Int) = "$year. godina"
}