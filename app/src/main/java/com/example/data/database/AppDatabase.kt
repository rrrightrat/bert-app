package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BlueprintEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BlueprintEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blueprintDao(): BlueprintDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bert_redstone.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialBlueprints(database.blueprintDao())
                    }
                }
            }
        }

        private suspend fun populateInitialBlueprints(dao: BlueprintDao) {
            val starterBlueprints = listOf(
                BlueprintEntity(
                    title = "Standard 41-Item ImpulseSV Filter",
                    category = "SORTER",
                    description = "Bedrock standard item sorter with 41 filter items and 4 renamed blocker items in the hopper. Safe against crosstalk up to 68 items.",
                    configData = "Layout: Slot 1: 41x target item, Slots 2-5: 1x blocker item each.\nResting Signal: SS 2 (45 items)\nActivation Signal: SS 3 (46 items -> unlocks lower hopper)\nOverflow Threshold: SS 4 (69 items -> danger!)",
                    isFavorite = true
                ),
                BlueprintEntity(
                    title = "30-Second Etho Hopper Clock",
                    category = "TIMING",
                    description = "Compact 2-hopper timer with redstone blocks and comparators. Perfect for automated farm flush cycles.",
                    configData = "Items required: 38 items (any stackable)\nOne-way transfer: 15.2s (152 redstone ticks)\nFull cycle period: 30.4s (304 redstone ticks)\nBedrock reliability: 100% stable across chunk reloads",
                    isFavorite = true
                ),
                BlueprintEntity(
                    title = "4-Piston Trident Killer Timing",
                    category = "MECHANICS",
                    description = "High-efficiency Bedrock Trident Killer loop using 4 regular pistons and 4 observers or repeaters.",
                    configData = "Loop timing: 4-tick repeater chain per corner\nDamage rate: ~1.8 hits/sec\nNote: Holding a Looting III sword applies to trident damage in Bedrock!",
                    isFavorite = true
                ),
                BlueprintEntity(
                    title = "8-TNT Propellant Cannon Fuse Alignment",
                    category = "BALLISTICS",
                    description = "Long-range TNT cannon propellant array with payload sync delay for maximum horizontal velocity.",
                    configData = "Propellant: 8 Dispenser-primed TNT in water chamber\nDelay to payload: 36 redstone ticks (72 game ticks)\nEstimated reach: 240+ blocks\nBedrock note: Water prevents block destruction while preserving velocity vector",
                    isFavorite = false
                ),
                BlueprintEntity(
                    title = "Crafter SS 5 Logic Signal",
                    category = "COMPARATOR",
                    description = "Using Minecraft 1.21 Crafter disabled slots to output constant analog signal strength 5.",
                    configData = "Container: Crafter (9 slots)\nConfiguration: Disable exactly 5 slots (click each slot to toggle red)\nComparator Output: Exactly Signal Strength 5\nAdvantage: Zero item lag, completely immovable by hopper siphon",
                    isFavorite = false
                )
            )
            dao.insertAll(starterBlueprints)
        }
    }
}
