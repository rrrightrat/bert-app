package com.example.data.database

import com.example.data.model.BlueprintEntity
import kotlinx.coroutines.flow.Flow

class BlueprintRepository(private val dao: BlueprintDao) {
    val allBlueprints: Flow<List<BlueprintEntity>> = dao.getAllBlueprints()

    fun getByCategory(category: String): Flow<List<BlueprintEntity>> =
        dao.getBlueprintsByCategory(category)

    suspend fun insert(blueprint: BlueprintEntity): Long = dao.insertBlueprint(blueprint)

    suspend fun update(blueprint: BlueprintEntity) = dao.updateBlueprint(blueprint)

    suspend fun delete(blueprint: BlueprintEntity) = dao.deleteBlueprint(blueprint)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun toggleFavorite(id: Long, current: Boolean) = dao.setFavorite(id, !current)

    suspend fun getCount(): Int = dao.getCount()
}
