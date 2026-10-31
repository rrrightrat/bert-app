package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a saved redstone configuration, calculation, or contraption blueprint.
 */
@Entity(tableName = "blueprints")
data class BlueprintEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "COMPARATOR", "TIMING", "SORTER", "BALLISTICS", "LOGIC", "MECHANICS"
    val description: String,
    val configData: String, // JSON or formatted summary string of settings
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
