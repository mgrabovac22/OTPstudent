package hr.wortex.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Singleton registar koji drži listu svih dostupnih metoda za otključavanje.
 * Moduli koji pružaju metode se ovdje registriraju, a :app modul odavde čita.
 */
object UnlockMethodRegistry {
    private val _methods = mutableStateListOf<UnlockMethod>()
    val methods: SnapshotStateList<UnlockMethod> = _methods

    fun register(method: UnlockMethod) {
        if (_methods.none { it.name == method.name }) {
            _methods.add(method)
        }
    }
}