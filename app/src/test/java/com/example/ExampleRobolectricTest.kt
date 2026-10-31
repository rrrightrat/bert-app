package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.model.BlueprintEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun readStringFromContext_matchesAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BERT", appName)
    }

    @Test
    fun insertAndRetrieveBlueprint_succeeds() = runBlocking {
        val dao = db.blueprintDao()
        val item = BlueprintEntity(
            title = "Test Piston Door Timing",
            category = "TIMING",
            description = "Double piston extender delay",
            configData = "Piston 1: 0 tick, Piston 2: 2 tick repeater"
        )
        val id = dao.insertBlueprint(item)
        assertTrue(id > 0)

        val retrieved = dao.getBlueprintById(id)
        assertEquals("Test Piston Door Timing", retrieved?.title)
        assertEquals("TIMING", retrieved?.category)

        dao.setFavorite(id, true)
        val updated = dao.getBlueprintById(id)
        assertEquals(true, updated?.isFavorite)

        val list = dao.getAllBlueprints().first()
        assertEquals(1, list.size)
    }
}
