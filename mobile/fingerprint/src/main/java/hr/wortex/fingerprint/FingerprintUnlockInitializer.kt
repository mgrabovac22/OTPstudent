package hr.wortex.fingerprint

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import hr.wortex.core.UnlockMethodRegistry

class FingerprintUnlockInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        UnlockMethodRegistry.register(FingerprintUnlock)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}