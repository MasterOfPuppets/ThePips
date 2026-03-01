package com.masterofpuppets.thepips.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    val isGlobalPipsEnabled: Flow<Boolean>
    suspend fun setGlobalPipsEnabled(enabled: Boolean)
}