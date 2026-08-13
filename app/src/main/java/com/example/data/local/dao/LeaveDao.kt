package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.LeaveRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_requests ORDER BY requestedAt DESC")
    fun getAllLeaveRequests(): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getLeaveRequestsForUser(userId: Int): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE status = :status ORDER BY requestedAt DESC")
    fun getLeaveRequestsByStatus(status: String): Flow<List<LeaveRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(leave: LeaveRequestEntity): Long

    @Update
    suspend fun updateLeaveRequest(leave: LeaveRequestEntity)
}
