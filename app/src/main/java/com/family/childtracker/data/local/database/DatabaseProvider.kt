package com.family.childtracker.data.local.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: ChildTrackerDatabase? = null

    fun getDatabase(context: Context): ChildTrackerDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ChildTrackerDatabase::class.java,
                "child_tracker_database"
            )
                .fallbackToDestructiveMigration() // For initial development
                // TODO: Add proper migrations before production release
                .build()
            INSTANCE = instance
            instance
        }
    }
}
