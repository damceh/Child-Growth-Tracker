package com.family.childtracker.data.local.database

import android.content.Context
import androidx.room.Room

class DatabaseProvider(private val context: Context) {
    fun getDatabase(): ChildTrackerDatabase {
        return DatabaseProvider.getDatabase(context)
    }

    companion object {
        @Volatile
        private var INSTANCE: ChildTrackerDatabase? = null
        
        @Volatile
        private var PROVIDER_INSTANCE: DatabaseProvider? = null

        fun getDatabase(context: Context): ChildTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChildTrackerDatabase::class.java,
                    "child_tracker_database"
                )
                    .addMigrations(
                        Migrations.MIGRATION_1_2,
                        Migrations.MIGRATION_2_3,
                        Migrations.MIGRATION_3_4
                    )
                    .fallbackToDestructiveMigration() // For initial development
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        fun getInstance(context: Context): DatabaseProvider {
            return PROVIDER_INSTANCE ?: synchronized(this) {
                val instance = DatabaseProvider(context.applicationContext)
                PROVIDER_INSTANCE = instance
                instance
            }
        }
    }
}
