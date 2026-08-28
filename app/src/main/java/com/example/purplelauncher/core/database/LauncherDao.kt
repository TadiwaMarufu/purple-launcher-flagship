package com.example.purplelauncher.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {
    // App Custom Data
    @Query("SELECT * FROM app_custom_data")
    fun getAllAppCustomData(): Flow<List<AppCustomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppCustom(entity: AppCustomEntity)

    @Query("UPDATE app_custom_data SET launchCount = launchCount + 1, lastLaunchedTime = :timestamp WHERE packageName = :packageName")
    suspend fun incrementAppLaunch(packageName: String, timestamp: Long)

    @Query("UPDATE app_custom_data SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun updateFavorite(packageName: String, isFavorite: Boolean)

    @Query("UPDATE app_custom_data SET isHidden = :isHidden WHERE packageName = :packageName")
    suspend fun updateHidden(packageName: String, isHidden: Boolean)

    @Query("UPDATE app_custom_data SET customLabel = :label WHERE packageName = :packageName")
    suspend fun updateCustomLabel(packageName: String, label: String?)

    // Profiles
    @Query("SELECT * FROM profiles ORDER BY createdTimestamp ASC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfileById(id: String)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    // Spaces
    @Query("SELECT * FROM spaces")
    fun getAllSpaces(): Flow<List<SpaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpace(space: SpaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpaces(spaces: List<SpaceEntity>)

    @Query("DELETE FROM spaces WHERE id = :id")
    suspend fun deleteSpaceById(id: String)

    // Folders
    @Query("SELECT * FROM folders")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: String)

    // Widget Stacks
    @Query("SELECT * FROM widget_stacks")
    fun getAllWidgetStacks(): Flow<List<WidgetStackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgetStack(stack: WidgetStackEntity)

    @Query("DELETE FROM widget_stacks WHERE id = :id")
    suspend fun deleteWidgetStackById(id: String)
}
