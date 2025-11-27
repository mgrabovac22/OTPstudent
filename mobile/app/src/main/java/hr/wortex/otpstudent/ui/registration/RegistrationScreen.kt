package hr.wortex.otpstudent.ui.registration

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hr.wortex.otpstudent.R
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.ui.registration.utils.DateUtils
import hr.wortex.otpstudent.ui.registration.utils.ValidationUtils
import java.util.Calendar

@Composable
fun RegistrationScreen(paddingValues: PaddingValues, navController: NavController) {

    val viewModel: RegistrationScreenViewModel = viewModel(factory = RegistrationViewModelFactory(
        DependencyProvider.register, DependencyProvider.getAllInstitutions))
    val uiState = viewModel.uiState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var yearOfStudy by remember { mutableStateOf("") }
    var areaOfStudy by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var higherEducationBody by remember { mutableStateOf("") }
    var higherEducationBodyID by remember { mutableIntStateOf(0) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var yearOfStudyError by remember { mutableStateOf<String?>(null) }
    var areaOfStudyError by remember { mutableStateOf<String?>(null) }
    var dateOfBirthError by remember { mutableStateOf<String?>(null) }
    var higherEducationBodyError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    val higherEducationBodyOptions by viewModel.institutions.collectAsState()

    LaunchedEffect(uiState.value) {
        when (val state = uiState.value) {
            is RegisterUiState.Success -> {
                navController.navigate("home_screen") {
                    popUpTo("registration_screen") { inclusive = true }
                }
            }
            is RegisterUiState.Error -> {
                generalError = (uiState.value as RegisterUiState.Error).message
            }
            else -> {
                generalError = null
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF056f52)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.otp),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text("OTP", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text("Student", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFf2701b))
            }

            Spacer(modifier = Modifier.height(20.dp))

            //First name
            CreateTextInput(
                label = "Ime",
                value = firstName,
                isVisible = true,
                onValueChange = {
                    firstName = it
                    firstNameError = null
                },
                error = firstNameError,
                leadingIcon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Last name
            CreateTextInput(
                label = "Prezime",
                value = lastName,
                isVisible = true,
                onValueChange = {
                    lastName = it
                    lastNameError = null
                },
                error = lastNameError,
                leadingIcon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Email
            CreateTextInput(
                label = "Email",
                value = email,
                isVisible = true,
                onValueChange = {
                    email = it
                    emailError = null
                },
                error = emailError,
                leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Password
            CreateTextInput(
                label = "Lozinka",
                value = password,
                isVisible = false,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                error = passwordError,
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Confirm password
            CreateTextInput(
                label = "Potvrdi lozinku",
                value = confirmPassword,
                isVisible = false,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                error = confirmPasswordError,
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Area of study
            CreateTextInput(
                label = "Smjer studija",
                value = areaOfStudy,
                isVisible = true,
                onValueChange = {
                    areaOfStudy = it
                    areaOfStudyError = null
                },
                error = areaOfStudyError,
                leadingIcon = { Icon(Icons.Rounded.Create, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Year of study
            CreateTextInput(
                label = "Godina studija",
                value = yearOfStudy,
                isVisible = true,
                onValueChange = {
                    yearOfStudy = it
                    yearOfStudyError = null
                },
                error = yearOfStudyError,
                leadingIcon = { Icon(Icons.Rounded.DateRange, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Date of birth
            CreateDatePickerInput(
                dateOfBirth = dateOfBirth,
                onDateSelected = { selected ->
                    dateOfBirth = selected
                    dateOfBirthError = null
                },
                error = dateOfBirthError
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Higher education body
            CreateDropdownMenu(
                value = higherEducationBody,
                label = "Visoko obrazovno tijelo",
                ID = higherEducationBodyID,
                options = higherEducationBodyOptions,
                onValueChange = { selectedKey, selectedValue ->
                    higherEducationBody = selectedValue
                    higherEducationBodyID = selectedKey
                    higherEducationBodyError = null
                },
                error = higherEducationBodyError
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    var isValid = true

                    if (!ValidationUtils.isLongerThanOneCharacter(firstName)) {
                        firstNameError = "Ime mora imati više od jednog znaka"
                        isValid = false
                    }

                    if (!ValidationUtils.isLongerThanOneCharacter(lastName)) {
                        lastNameError = "Prezime mora imati više od jednog znaka"
                        isValid = false
                    }

                    if (!ValidationUtils.isEmailValid(email)) {
                        emailError = "Email mora završavati s @student.foi.hr"
                        isValid = false
                    }

                    if (!ValidationUtils.isPasswordValid(password)) {
                        passwordError = "Lozinka mora biti između 8 i 64 znaka"
                        isValid = false
                    }

                    if (!ValidationUtils.doPasswordsMatch(password,confirmPassword)) {
                        confirmPasswordError = "Lozinke se ne podudaraju"
                        isValid = false
                    }

                    if (ValidationUtils.isEmpty(areaOfStudy)) {
                        areaOfStudyError = "Smjer ne može biti prazan"
                        isValid = false
                    }

                    val year = yearOfStudy.toIntOrNull()
                    if (year == null || year !in 1..5) {
                        yearOfStudyError = "Godina studija mora biti između 1 i 5"
                        isValid = false
                    }

                    var formattedDate = ""

                    if (dateOfBirth.isBlank()) {
                        dateOfBirthError = "Unesite datum rođenja"
                        isValid = false
                    } else {
                        if (!DateUtils.isNotInFuture(dateOfBirth)) {
                            dateOfBirthError = "Datum ne smije biti u budućnosti"
                            isValid = false
                        } else if (!DateUtils.isAtLeast18(dateOfBirth)) {
                            dateOfBirthError = "Morate imati više od 18 godina"
                            isValid = false
                        } else {
                            formattedDate = DateUtils.formatForApi(dateOfBirth)
                        }
                    }

                    if (higherEducationBodyID == 0) {
                        higherEducationBodyError = "Odaberite obrazovno tijelo"
                        isValid = false
                    }

                    if (isValid) {
                        viewModel.registerUser(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            hashPassword = password,
                            yearOfStudy = yearOfStudy.toIntOrNull()!!,
                            areaOfStudy = areaOfStudy,
                            dateOfStudy = formattedDate,
                            higherEducationBodyID = higherEducationBodyID
                        )


                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 90.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
            ) {
                if (uiState.value == RegisterUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Text(text = "Registracija")
                }
            }

            if (generalError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = generalError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text("Imaš račun? ", color = Color(0xFFf2701b))
                Text(
                    "Prijavi se!",
                    color = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate("login_screen")
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun CreateTextInput(
    label: String,
    value: String,
    isVisible: Boolean,
    onValueChange: (String) -> Unit,
    error: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Unspecified) },
        leadingIcon = leadingIcon,
        isError = error != null,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.White
        ),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
    )

    if (error != null) {
        Text(
            text = error ?: "",
            color = Color.Red,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDatePickerInput(
    dateOfBirth: String,
    onDateSelected: (String) -> Unit,
    error: String? = null
) {
    val context = LocalContext.current
    val latestAllowed = remember { DateUtils.latestAllowedBirthDateMillis() }

    val openDatePicker = remember (dateOfBirth, latestAllowed) {
        {
            val initialMillis = hr.wortex.otpstudent.ui.profil.edit.DateUtils.displayToMillis(dateOfBirth)?.let {
                if (it <= latestAllowed) it else latestAllowed
            } ?: latestAllowed

            val cal = Calendar.getInstance().apply { timeInMillis = initialMillis }

            DatePickerDialog(
                context,
                { _, y, m, d ->
                    val day = d.toString().padStart(2, '0')
                    val month = (m + 1).toString().padStart(2, '0')
                    onDateSelected("$day.$month.$y.")
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { openDatePicker() },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.DateRange,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Datum rođenja",
                        fontSize = 12.sp,
                        color = Color.Unspecified
                    )

                    Text(
                        text = dateOfBirth.ifBlank { "Unesite datum rođenja" },
                        fontSize = 16.sp,
                        color = if (dateOfBirth.isBlank()) Color.DarkGray else Color.Black,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }

    if (error != null) {
        Text(
            text = error,
            color = Color.Red,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
    }
}


@Composable
fun CreateDropdownMenu(
    value: String,
    label: String,
    ID: Int,
    options: Map<Int,String>,
    onValueChange: (Int,String) -> Unit,
    error: String? = null,
){
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { showDialog = true },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = if (error != null) Color.Red else Color.Unspecified
                    )

                    Text(
                        text = value.ifBlank { "Odaberite obrazovno tijelo" },
                        fontSize = 16.sp,
                        color = if (value.isBlank()) Color.DarkGray else Color.Black,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            text = {
                Column {
                    Text("Odaberite obrazovno tijelo", fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    options.forEach { (id, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(id, label)
                                    showDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (id == ID),
                                onClick = {
                                    onValueChange(id, label)
                                    showDialog = false
                                }
                            )
                            Text(text = label, fontSize = 16.sp)
                        }
                    }
                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegistrationScreenPreview(){
    val navController = rememberNavController()
    Scaffold { padding ->
        RegistrationScreen(paddingValues = padding, navController = navController)
    }
}