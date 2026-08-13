package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY fullName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'STAFF' ORDER BY fullName ASC")
    fun getAllStaff(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'")
    suspend fun getAdminCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STAFF' AND isActive = 1")
    fun getActiveStaffCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)

    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun setUserActiveStatus(userId: Int, isActive: Boolean)
}
