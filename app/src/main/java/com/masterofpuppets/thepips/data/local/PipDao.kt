package com.masterofpuppets.thepips.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PipDao {
    @Query("SELECT * FROM pips_table ORDER BY name ASC")
    fun getAllPips(): Flow<List<PipEntity>>

    @Query("SELECT * FROM pips_table WHERE id = :pipId")
    suspend fun getPipById(pipId: Long): PipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPip(pip: PipEntity): Long

    @Update
    suspend fun updatePip(pip: PipEntity): Int

    @Delete
    suspend fun deletePip(pip: PipEntity): Int
}