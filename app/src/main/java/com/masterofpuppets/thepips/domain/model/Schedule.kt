package com.masterofpuppets.thepips.domain.model

data class Schedule(
    val id: Long = 0,
    val pipId: Long,
    // Bit 0 to 59 represents each minute of the hour. Default 1L means bit 0 (Top of the hour).
    val minuteMask: Long = 1L,
    // Bits 0 to 6 represent Mon to Sun. Default 127 (binary 01111111) means all 7 days.
    val daysOfWeekMask: Int = 127,
    // 0 to 23
    val startHour: Int = 0,
    // 0 to 23
    val endHour: Int = 23
) {
    /**
     * Evaluates if the given time matches this schedule's rules.
     *
     * @param minute Current minute (0-59)
     * @param dayOfWeek Current day of the week (0=Mon, 1=Tue, ..., 6=Sun)
     * @param hour Current hour (0-23)
     */
    fun isMatch(minute: Int, dayOfWeek: Int, hour: Int): Boolean {
        val isMinuteMatch = (minuteMask and (1L shl minute)) != 0L
        val isDayMatch = (daysOfWeekMask and (1 shl dayOfWeek)) != 0
        val isHourMatch = if (startHour <= endHour) {
            hour in startHour..endHour
        } else {
            hour >= startHour || hour <= endHour
        }

        return isMinuteMatch && isDayMatch && isHourMatch
    }
}