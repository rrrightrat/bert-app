package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.BlueprintRepository
import com.example.data.model.BlueprintEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val iconLabel: String) {
    HOME("Home", "HOME"),
    COMPARATOR("Comparator", "CMP"),
    TIMING("Timings", "CLK"),
    SORTER("Sorters", "SRT"),
    BALLISTICS("Ballistics", "TNT"),
    LOGIC("Logic", "LOGIC"),
    MECHANICS("Quirks", "MCBE"),
    BLUEPRINTS("Presets", "PRESET"),
    ABOUT("About", "ABOUT")
}

class RedstoneViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = BlueprintRepository(database.blueprintDao())

    val allBlueprints: StateFlow<List<BlueprintEntity>> = repository.allBlueprints
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Starting on HOME screen as requested
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun saveBlueprint(title: String, category: String, description: String, configData: String) {
        viewModelScope.launch {
            val blueprint = BlueprintEntity(
                title = title,
                category = category,
                description = description,
                configData = configData,
                isFavorite = false
            )
            repository.insert(blueprint)
            _snackbarMessage.emit("Saved '$title' to Blueprints!")
        }
    }

    fun toggleFavorite(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, current)
        }
    }

    fun deleteBlueprint(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            _snackbarMessage.emit("Blueprint deleted")
        }
    }
}
