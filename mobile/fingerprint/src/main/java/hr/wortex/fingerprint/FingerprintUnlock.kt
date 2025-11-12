package hr.wortex.fingerprint

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import hr.wortex.core.UnlockMethod
import hr.wortex.core.UnlockResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FingerprintUnlock : UnlockMethod {

    override val name: String = "fingerprint"

    override suspend fun launchUnlock(
        activity: FragmentActivity,
        checkHasTokens: suspend () -> Boolean
    ): UnlockResult {
        if (!checkHasTokens()) {
            return UnlockResult.Error("Nema spremljene sesije za prijavu otiskom.")
        }

        // Pretvaramo callback-based API u suspend funkciju
        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(activity)

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (continuation.isActive) {
                        continuation.resume(UnlockResult.Success)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (continuation.isActive) {
                        continuation.resume(UnlockResult.Error(errString.toString()))
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Prompt ostaje na ekranu, ne radimo ništa
                }
            }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Otključavanje otiskom prsta")
                .setSubtitle("Potvrdite identitet")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Odustani")
                .build()

            val prompt = BiometricPrompt(activity, executor, callback)

            continuation.invokeOnCancellation {
                prompt.cancelAuthentication()
            }

            prompt.authenticate(promptInfo)
        }
    }
}