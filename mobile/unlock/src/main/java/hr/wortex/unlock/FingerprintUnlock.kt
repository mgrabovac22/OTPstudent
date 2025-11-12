package hr.wortex.unlock

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
        // Prvo provjeravamo imamo li uopće spremljenu sesiju
        if (!checkHasTokens()) {
            return UnlockResult.Error("Nema spremljene sesije za prijavu otiskom.")
        }

        // Pretvaramo callback-based API u suspend funkciju
        return suspendCancellableCoroutine { continuation ->
            BiometricHelper.prompt(
                activity = activity,
                onSuccess = {
                    // Nastavljamo coroutine s uspješnim rezultatom
                    if (continuation.isActive) {
                        continuation.resume(UnlockResult.Success)
                    }
                },
                onError = { msg ->
                    // Nastavljamo coroutine s greškom
                    if (continuation.isActive) {
                        continuation.resume(UnlockResult.Error(msg))
                    }
                }
            )
        }
    }
}