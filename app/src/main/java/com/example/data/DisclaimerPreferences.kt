package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.chickCheckDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "chickcheck_preferences"
)

interface DisclaimerStore {
    val isDisclaimerAccepted: Flow<Boolean>
    suspend fun acceptDisclaimer()
}

class DataStoreDisclaimerStore(
    private val context: Context
) : DisclaimerStore {

    private object Keys {
        val DISCLAIMER_ACCEPTED = booleanPreferencesKey("disclaimer_accepted")
    }

    override val isDisclaimerAccepted: Flow<Boolean> =
        context.chickCheckDataStore.data.map { preferences ->
            preferences[Keys.DISCLAIMER_ACCEPTED] ?: false
        }

    override suspend fun acceptDisclaimer() {
        context.chickCheckDataStore.edit { preferences ->
            preferences[Keys.DISCLAIMER_ACCEPTED] = true
        }
    }
}
