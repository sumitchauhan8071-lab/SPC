package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.DepartmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(dept: DepartmentEntity): Long

    @Update
    suspend fun updateDepartment(dept: DepartmentEntity)

    @Query("DELETE FROM departments WHERE id = :id")
    suspend fun deleteDepartment(id: Int)
}
