package hr.wortex.otpstudent.ui.registration

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hr.wortex.otpstudent.R
import java.util.Calendar

@Composable
fun RegistrationScreen(paddingValues: PaddingValues, navController: NavController){
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

    // TODO: Replace with API call 
    var higherEducationBodyOptions = mapOf<Int, String>(
        1 to "Fakultet organizacije i informatike",
        2 to "Fakultet elektrotehnike i računarstva",
        3 to "Sveučilište u Zagrebu"
    )

    var isPopupVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF056f52)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
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
                onDateSelected = { selectedDate ->
                    dateOfBirth = selectedDate
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
                },
                error = higherEducationBodyError,
                leadingIcon = { Icon(Icons.Rounded.Home, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    // TODO: Validate registration
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 90.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
            ) {
                // TODO: Implement uiState
                Text(text = "Registracija")
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

        if(isPopupVisible){

        }
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
        label = { Text(error ?: label, color = if (error != null) Color.Red else Color.Unspecified) },
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

@OptIn(ExperimentalMaterial3Api::class) // TODO: fix
@Composable
fun CreateDatePickerInput(
    dateOfBirth: String,
    onDateSelected: (String) -> Unit,
    error: String? = null
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            LocalContext.current,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        ).show()
    }

    TextField(
        value = dateOfBirth,
        onValueChange = {},
        label = { Text(error ?: "Datum rođenja", color = if (error != null) Color.Red else Color.Unspecified) },
        readOnly = true,
        leadingIcon = { Icon(Icons.Rounded.DateRange, contentDescription = null) },
        isError = error != null,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.White
        )
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

@Composable
fun CreateDropdownMenu(
    value: String,
    label: String,
    ID: Int,
    options: Map<Int,String>,
    onValueChange: (Int,String) -> Unit,
    error: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null
){
    var selectedKey by remember { mutableIntStateOf(ID) }
    var selectedValue by remember { mutableStateOf(value) }

    TextField(
        value = selectedValue,
        onValueChange = {
            onValueChange(selectedKey, it)
        },
        label = { Text(error ?: label, color = if (error != null) Color.Red else Color.Unspecified) },
        readOnly = true,
        leadingIcon = leadingIcon,
        isError = error != null,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.White
        )
    )

    // TODO: create dropdown or popup to show possible values from options
}