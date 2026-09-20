package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlayerProfileEntity::class, AchievementRecord::class],
    version = 1,
    exportSchema = false
)
abstract class MathBrawlDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    companion object {
        @Volatile
        private var INSTANCE: MathBrawlDatabase? = null

        fun getInstance(context: Context): MathBrawlDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MathBrawlDatabase::class.java,
                    "math_brawl_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
