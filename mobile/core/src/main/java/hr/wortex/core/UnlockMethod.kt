package hr.wortex.core

import androidx.fragment.app.FragmentActivity

// Definira mogući ishod procesa otključavanja
sealed class UnlockResult {
    object Success : UnlockResult()
    data class Error(val message: String) : UnlockResult()
}

/**
 * Predstavlja jednu metodu za otključavanje ekrana (npr. otisak prsta, PIN, prepoznavanje lica...).
 * Svaki modul koji nudi otključavanje mora implementirati ovo sučelje.
 */
interface UnlockMethod {
    /**
     * Jedinstveni naziv metode, npr. "fingerprint".
     */
    val name: String

    /**
     * Pokreće proces otključavanja za ovu metodu.
     * Ovo je suspend funkcija koja obavlja asinkroni posao i vraća rezultat.
     *
     * @param activity Kontekst aktivnosti, potreban za pokretanje npr. BiometricPrompt-a.
     * @param checkHasTokens Funkcija koja provjerava postoje li spremljeni tokeni.
     * @return UnlockResult koji označava uspjeh ili neuspjeh.
     */
    suspend fun launchUnlock(activity: FragmentActivity, checkHasTokens: suspend () -> Boolean): UnlockResult
}
