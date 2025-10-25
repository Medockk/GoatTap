package com.example.goadtap

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CoinData::class], version = 3, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun coinDataDao(): CoinDataDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database3")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}