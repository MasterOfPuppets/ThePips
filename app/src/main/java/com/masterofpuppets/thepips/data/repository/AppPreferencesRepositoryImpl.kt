package com.masterofpuppets.thepips.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.masterofpuppets.thepips.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppPreferencesRepository {

    private companion object {
        val GLOBAL_PIPS_ENABLED_KEY = booleanPreferencesKey("global_pips_enabled")
    }

    override val isGlobalPipsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[GLOBAL_PIPS_ENABLED_KEY] ?: false // Default state is disabled
    }

    override suspend fun setGlobalPipsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[GLOBAL_PIPS_ENABLED_KEY] = enabled
        }
    }
}