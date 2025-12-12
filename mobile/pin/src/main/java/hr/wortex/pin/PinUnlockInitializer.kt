package hr.wortex.pin

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import hr.wortex.core.UnlockMethodRegistry
//import hr.wortex.fingerprint.FingerprintUnlockInitializer

class PinUnlockInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        UnlockMethodRegistry.register(PinUnlock)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
        //return listOf(FingerprintUnlockInitializer::class.java)
    }
}