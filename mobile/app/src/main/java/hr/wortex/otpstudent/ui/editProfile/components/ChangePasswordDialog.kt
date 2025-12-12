package hr.wortex.otpstudent.ui.editProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.wortex.otpstudent.ui.profil.edit.EditProfileViewModel

@Composable
fun ChangePasswordDialog(
    viewModel: EditProfileViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val CustomGreen = Color(0xFF1B6E2A)
    val AppOrange = Color(0xFFf2701b)
    val White = Color.White
    val Black = Color.Black

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = CustomGreen,
        focusedLabelColor = CustomGreen,
        cursorColor = CustomGreen,
        unfocusedBorderColor = CustomGreen,
        unfocusedLabelColor = CustomGreen,

        errorBorderColor = MaterialTheme.colorScheme.error,
        errorLabelColor = MaterialTheme.colorScheme.error,

        focusedTextColor = Black,
        unfocusedTextColor = Black
    )

    if (state.isPasswordChanged) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Lozinka promijenjena", fontWeight = FontWeight.Bold, color = CustomGreen) },
            text = { Text("Vaša lozinka je uspješno promijenjena.") },
            containerColor = White,
            textContentColor = Black,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK", color = CustomGreen)
                }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = {},
        containerColor = White,
        tonalElevation = 6.dp,
        title = { Text("Promijeni lozinku", fontWeight = FontWeight.Bold, color = CustomGreen) },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {

                OutlinedTextField(
                    value = state.oldPasswordInput,
                    onValueChange = viewModel::onOldPasswordChange,
                    label = { Text("Stara lozinka") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = customTextFieldColors,
                    visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (showOldPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { showOldPassword = !showOldPassword }) {
                            Icon(icon, contentDescription = null, tint = CustomGreen)
                        }
                    },
                    isError = state.oldPasswordError != null
                )
                state.oldPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.newPasswordInput,
                    onValueChange = viewModel::onNewPasswordChange,
                    label = { Text("Nova lozinka") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = customTextFieldColors,
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (showNewPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(icon, contentDescription = null, tint = CustomGreen)
                        }
                    },
                    isError = state.newPasswordError != null
                )
                state.newPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.newPasswordConfirmInput,
                    onValueChange = viewModel::onNewPasswordConfirmChange,
                    label = { Text("Potvrda nove lozinke") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = customTextFieldColors,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(icon, contentDescription = null, tint = CustomGreen)
                        }
                    },
                    isError = state.newPasswordConfirmError != null
                )
                state.newPasswordConfirmError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

                state.changePasswordErrorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.confirmPasswordChange() },
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppOrange,
                    contentColor = Color.White
                )
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("Potvrdi")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.hidePasswordDialog()
                onDismiss()
            }) {
                Text("Odustani", color = CustomGreen)
            }
        }
    )
}