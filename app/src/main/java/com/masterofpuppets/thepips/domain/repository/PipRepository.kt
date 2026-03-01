package com.masterofpuppets.thepips.domain.repository

import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.model.PipSchedule
import com.masterofpuppets.thepips.domain.model.Schedule
import kotlinx.coroutines.flow.Flow

interface PipRepository {

    // --- Pip Operations ---
    fun getAllPips(): Flow<List<Pip>>

    suspend fun getPipById(id: Long): Pip?

    suspend fun insertPip(pip: Pip): Long

    suspend fun updatePip(pip: Pip)

    suspend fun deletePip(pip: Pip)


    // --- Schedule Operations ---
    fun getAllSchedules(): Flow<List<PipSchedule>>

    fun getSchedulesForPip(pipId: Long): Flow<List<Schedule>>

    suspend fun getScheduleById(id: Long): Schedule?

    suspend fun insertSchedule(schedule: Schedule): Long

    suspend fun updateSchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)
}