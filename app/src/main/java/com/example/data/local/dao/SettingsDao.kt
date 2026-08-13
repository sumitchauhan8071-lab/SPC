package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.OrgSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM org_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<OrgSettingsEntity?>

    @Query("SELECT * FROM org_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): OrgSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: OrgSettingsEntity)
}
