package com.family.childtracker.presentation.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manager for auto-saving form drafts to prevent data loss
 */
class AutoSaveManager(private val context: Context) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "form_drafts")
    
    /**
     * Save a form draft
     */
    suspend fun <T> saveDraft(formId: String, data: T, serializer: (T) -> String) {
        val key = stringPreferencesKey(formId)
        context.dataStore.edit { preferences ->
            preferences[key] = serializer(data)
        }
    }
    
    /**
     * Load a form draft
     */
    fun <T> loadDraft(formId: String, deserializer: (String) -> T?): Flow<T?> {
        val key = stringPreferencesKey(formId)
        return context.dataStore.data.map { preferences ->
            preferences[key]?.let { deserializer(it) }
        }
    }
    
    /**
     * Clear a form draft
     */
    suspend fun clearDraft(formId: String) {
        val key = stringPreferencesKey(formId)
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
    
    /**
     * Clear all drafts
     */
    suspend fun clearAllDrafts() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * Data class for growth record draft
 */
@Serializable
data class GrowthRecordDraft(
    val height: String = "",
    val weight: String = "",
    val headCircumference: String = "",
    val notes: String = "",
    val recordDate: Long = System.currentTimeMillis()
)

/**
 * Data class for milestone draft
 */
@Serializable
data class MilestoneDraft(
    val category: String = "",
    val description: String = "",
    val notes: String = "",
    val achievementDate: Long = System.currentTimeMillis()
)

/**
 * Data class for behavior entry draft
 */
@Serializable
data class BehaviorEntryDraft(
    val mood: String? = null,
    val sleepQuality: Int? = null,
    val eatingHabits: String? = null,
    val notes: String = "",
    val entryDate: Long = System.currentTimeMillis()
)

/**
 * Helper object for JSON serialization
 */
object DraftSerializer {
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    inline fun <reified T> serialize(data: T): String {
        return json.encodeToString(data)
    }
    
    inline fun <reified T> deserialize(jsonString: String): T? {
        return try {
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
}
