package hr.wortex.fingerprintunlock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch

@Composable
fun UnlockScreen(
    onUnlockSuccess: () -> Unit,
    onSessionError: (String) -> Unit,
    checkHasTokens: suspend () -> Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (checkHasTokens()) {
            val activity = context as? FragmentActivity
            if (activity != null) {
                BiometricHelper.prompt(
                    activity = activity,
                    onSuccess = {
                        scope.launch {
                            onUnlockSuccess()
                        }
                    },
                    onError = { msg -> onSessionError(msg) }
                )
            } else {
                onSessionError("Biometrija nije podržana u ovoj aktivnosti.")
            }
        } else {
             onSessionError("Nema spremljene sesije za prijavu otiskom.")
        }
    }
}
