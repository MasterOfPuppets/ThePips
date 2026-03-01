package com.masterofpuppets.thepips.data.repository

import com.masterofpuppets.thepips.data.local.PipDao
import com.masterofpuppets.thepips.data.local.ScheduleDao
import com.masterofpuppets.thepips.data.mapper.toPip
import com.masterofpuppets.thepips.data.mapper.toPipEntity
import com.masterofpuppets.thepips.data.mapper.toPipSchedule
import com.masterofpuppets.thepips.data.mapper.toSchedule
import com.masterofpuppets.thepips.data.mapper.toScheduleEntity
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.model.PipSchedule
import com.masterofpuppets.thepips.domain.model.Schedule
import com.masterofpuppets.thepips.domain.repository.PipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PipRepositoryImpl @Inject constructor(
    private val pipDao: PipDao,
    private val scheduleDao: ScheduleDao
) : PipRepository {

    override fun getAllPips(): Flow<List<Pip>> {
        return pipDao.getAllPips().map { entities ->
            entities.map { it.toPip() }
        }
    }

    override suspend fun getPipById(id: Long): Pip? {
        return pipDao.getPipById(id)?.toPip()
    }

    override suspend fun insertPip(pip: Pip): Long {
        return pipDao.insertPip(pip.toPipEntity())
    }

    override suspend fun updatePip(pip: Pip) {
        pipDao.updatePip(pip.toPipEntity())
    }

    override suspend fun deletePip(pip: Pip) {
        pipDao.deletePip(pip.toPipEntity())
    }

    override fun getAllSchedules(): Flow<List<PipSchedule>> {
        return scheduleDao.getAllSchedulesWithPips().map { entities ->
            entities.map { it.toPipSchedule() }
        }
    }

    override fun getSchedulesForPip(pipId: Long): Flow<List<Schedule>> {
        return scheduleDao.getSchedulesForPip(pipId).map { entities ->
            entities.map { it.toSchedule() }
        }
    }

    override suspend fun getScheduleById(id: Long): Schedule? {
        return scheduleDao.getScheduleById(id)?.toSchedule()
    }

    override suspend fun insertSchedule(schedule: Schedule): Long {
        return scheduleDao.insertSchedule(schedule.toScheduleEntity())
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule.toScheduleEntity())
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule.toScheduleEntity())
    }
}