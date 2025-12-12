package hr.wortex.otpstudent.ui.profil

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
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

private val AppGreen = Color(0xFF1B6E2A)
private val AppOrange = Color(0xFFf2701b)
private val White = Color.White
private val Black = Color.Black

private object ProfileDesign {
    val LogoTeal = Color(0xFF006B5C)
    val LogoOrange = Color(0xFFFF8C42)
    val CardBackground = Color(0xFFE8E8E8)
    val ButtonBackground = AppOrange
    val AvatarSize = 120.dp
    val CardHeight = 56.dp
    val PaddingSmall = 8.dp
    val PaddingMedium = 12.dp
    val PaddingLarge = 16.dp
    val PaddingExtraLarge = 24.dp
}

private object ProfileStrings {
    const val Title = "Profil"
    const val Edit = "Uredi"
    const val ErrorPrefix = "Greška: "
    const val FileNotSelected = "Nije odabrana nijedna datoteka"
    const val UploadCv = "Upload CV"
    const val SelectCv = "Odaberi CV"
    const val UploadSuccess = "CV uspješno učitan"
    const val FacultyName = "Fakultet organizacije i informatike"
    const val YearNotSet = "Godina nije upisana"
    const val AreaNotSet = ""
    fun yearOfStudyFormatted(year: Int) = "$year. godina"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onEditProfile: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = viewModel { ProfileViewModel(DependencyProvider.userRepository) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val context = LocalContext.current

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Custom(listOf("application/pdf")),
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            if (files.isNotEmpty()) {
                selectedUri = files.first().uri
                selectedFileName = selectedUri!!.getDisplayName(context) ?: "cv.pdf"
            } else {
                messageText = ProfileStrings.FileNotSelected
            }
        }
    )

    Scaffold(
        topBar = { ProfileTopBar(onEditProfile) },
        containerColor = White
    ) { paddingValues ->
        when (uiState) {
            is ProfileUiState.Loading -> ProfileLoadingScreen(paddingValues)
            is ProfileUiState.Error -> ProfileErrorScreen((uiState as ProfileUiState.Error).message, paddingValues)
            is ProfileUiState.Success -> {
                val user = (uiState as ProfileUiState.Success).user
                ProfileContent(
                    user = user,
                    selectedPdfName = selectedFileName,
                    messageText = messageText,
                    paddingValues = paddingValues,
                    onUploadClick = { pickerLauncher.launch() },
                    onUploadToServer = {
                        val uri = selectedUri
                        if (uri != null) {
                            val stream = context.contentResolver.openInputStream(uri)
                            if (stream != null) {
                                viewModel.uploadCV(stream)
                                messageText = ProfileStrings.UploadSuccess
                            } else {
                                messageText = "Greška: nije moguće otvoriti PDF."
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileLoadingScreen(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = AppGreen) }
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
        title = {
            Text(
                text = ProfileStrings.Title,
                fontWeight = FontWeight.Bold,
                color = White // Bijeli tekst
            )
        },
        actions = {
            IconButton(onClick = onEditProfile) {
                Icon(Icons.Default.Edit, contentDescription = ProfileStrings.Edit, tint = White) // Bijela ikona
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppGreen) // Zelena pozadina
    )
}

@Composable
private fun ProfileContent(
    user: UserProfile,
    selectedPdfName: String,
    onUploadClick: () -> Unit,
    paddingValues: PaddingValues,
    messageText: String,
    onUploadToServer: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(ProfileDesign.PaddingLarge)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(ProfileDesign.PaddingExtraLarge))

        Box(
            modifier = Modifier
                .size(ProfileDesign.AvatarSize)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            val imageBitmap: ImageBitmap? = remember(user.image) {
                user.image?.let { base64String ->
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
        }

        Spacer(Modifier.height(ProfileDesign.PaddingLarge))

        Text(
            text = "${user.firstName} ${user.lastName}",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Black
        )

        Spacer(Modifier.height(ProfileDesign.PaddingExtraLarge))

        ProfileInfoCard(user.email)
        Spacer(Modifier.height(ProfileDesign.PaddingMedium))
        ProfileInfoCard(ProfileStrings.FacultyName)
        Spacer(Modifier.height(ProfileDesign.PaddingMedium))
        ProfileInfoCard(user.yearOfStudy?.let { ProfileStrings.yearOfStudyFormatted(it) } ?: ProfileStrings.YearNotSet)
        Spacer(Modifier.height(ProfileDesign.PaddingMedium))
        ProfileInfoCard(user.areaOfStudy ?: ProfileStrings.AreaNotSet)

        Spacer(Modifier.height(ProfileDesign.PaddingMedium))

        UploadCvCard(selectedPdfName, onUploadClick)

        Spacer(Modifier.height(ProfileDesign.PaddingMedium))

        if (selectedPdfName.isNotEmpty()) {
            Button(
                onClick = onUploadToServer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ProfileDesign.PaddingLarge),
                colors = ButtonDefaults.buttonColors(containerColor = AppOrange), // Narančasti gumb
                shape = RoundedCornerShape(ProfileDesign.PaddingLarge)
            ) {
                Text(
                    text = ProfileStrings.UploadCv,
                    color = White, // Bijeli tekst gumba
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            Spacer(Modifier.height(ProfileDesign.PaddingSmall))
        }

        if (messageText.isNotEmpty()) {
            Text(
                text = messageText,
                color = if (messageText == ProfileStrings.UploadSuccess) AppGreen else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ProfileDesign.PaddingLarge)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun UploadCvCard(selectedPdfName: String, onUploadClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(ProfileDesign.CardHeight),
        shape = RoundedCornerShape(ProfileDesign.PaddingSmall),
        colors = CardDefaults.cardColors(containerColor = ProfileDesign.CardBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = ProfileDesign.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedPdfName.isEmpty()) ProfileStrings.UploadCv else selectedPdfName,
                fontSize = 16.sp,
                color = Black // Crni tekst
            )
            IconButton(onClick = onUploadClick) {
                Icon(Icons.Default.Edit, contentDescription = ProfileStrings.SelectCv, tint = Black.copy(alpha = 0.6f)) // Tamna ikona
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(ProfileDesign.CardHeight),
        shape = RoundedCornerShape(ProfileDesign.PaddingSmall),
        colors = CardDefaults.cardColors(containerColor = ProfileDesign.CardBackground)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = ProfileDesign.PaddingLarge),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = text, fontSize = 16.sp, color = Black)
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