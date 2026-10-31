package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BlueprintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlueprintDao {
    @Query("SELECT * FROM blueprints ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllBlueprints(): Flow<List<BlueprintEntity>>

    @Query("SELECT * FROM blueprints WHERE category = :category ORDER BY isFavorite DESC, createdAt DESC")
    fun getBlueprintsByCategory(category: String): Flow<List<BlueprintEntity>>

    @Query("SELECT * FROM blueprints WHERE id = :id LIMIT 1")
    suspend fun getBlueprintById(id: Long): BlueprintEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlueprint(blueprint: BlueprintEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(blueprints: List<BlueprintEntity>)

    @Update
    suspend fun updateBlueprint(blueprint: BlueprintEntity)

    @Delete
    suspend fun deleteBlueprint(blueprint: BlueprintEntity)

    @Query("DELETE FROM blueprints WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE blueprints SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM blueprints")
    suspend fun getCount(): Int
}
