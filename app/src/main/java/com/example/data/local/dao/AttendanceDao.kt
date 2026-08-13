package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC, checkInTimestamp DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY checkInTimestamp DESC")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY date DESC")
    fun getAttendanceForUser(userId: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getAttendanceForUserAndDate(userId: Int, date: String): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE date LIKE :monthPattern ORDER BY date ASC")
    fun getAttendanceForMonth(monthPattern: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Query("DELETE FROM attendance WHERE id = :id")
    suspend fun deleteAttendance(id: Int)
}
