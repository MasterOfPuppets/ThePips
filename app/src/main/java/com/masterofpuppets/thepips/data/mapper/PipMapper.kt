package com.masterofpuppets.thepips.data.mapper

import com.masterofpuppets.thepips.data.local.PipEntity
import com.masterofpuppets.thepips.data.local.ScheduleEntity
import com.masterofpuppets.thepips.data.local.ScheduleWithPip
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.model.PipSchedule
import com.masterofpuppets.thepips.domain.model.Schedule

fun PipEntity.toPip(): Pip {
    return Pip(
        id = id,
        name = name,
        isActive = isActive,
        globalVolume = globalVolume,
        slot1Hz = slot1Hz,
        slot1DurationMs = slot1DurationMs,
        slot2Hz = slot2Hz,
        slot2DurationMs = slot2DurationMs,
        slot3Hz = slot3Hz,
        slot3DurationMs = slot3DurationMs,
        slot4Hz = slot4Hz,
        slot4DurationMs = slot4DurationMs,
        slot5Hz = slot5Hz,
        slot5DurationMs = slot5DurationMs,
        slot6Hz = slot6Hz,
        slot6DurationMs = slot6DurationMs,
        slot7Hz = slot7Hz,
        slot7DurationMs = slot7DurationMs,
        slot8Hz = slot8Hz,
        slot8DurationMs = slot8DurationMs
    )
}

fun Pip.toPipEntity(): PipEntity {
    return PipEntity(
        id = id,
        name = name,
        isActive = isActive,
        globalVolume = globalVolume,
        slot1Hz = slot1Hz,
        slot1DurationMs = slot1DurationMs,
        slot2Hz = slot2Hz,
        slot2DurationMs = slot2DurationMs,
        slot3Hz = slot3Hz,
        slot3DurationMs = slot3DurationMs,
        slot4Hz = slot4Hz,
        slot4DurationMs = slot4DurationMs,
        slot5Hz = slot5Hz,
        slot5DurationMs = slot5DurationMs,
        slot6Hz = slot6Hz,
        slot6DurationMs = slot6DurationMs,
        slot7Hz = slot7Hz,
        slot7DurationMs = slot7DurationMs,
        slot8Hz = slot8Hz,
        slot8DurationMs = slot8DurationMs
    )
}

fun ScheduleEntity.toSchedule(): Schedule {
    return Schedule(
        id = id,
        pipId = pipId,
        minuteMask = minuteMask,
        daysOfWeekMask = daysOfWeekMask,
        startHour = startHour,
        endHour = endHour
    )
}

fun Schedule.toScheduleEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = id,
        pipId = pipId,
        minuteMask = minuteMask,
        daysOfWeekMask = daysOfWeekMask,
        startHour = startHour,
        endHour = endHour
    )
}

fun ScheduleWithPip.toPipSchedule(): PipSchedule {
    return PipSchedule(
        schedule = schedule.toSchedule(),
        pip = pip.toPip()
    )
}