package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ChickenFoodEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ChickenFoodDatabase : RoomDatabase() {
    abstract fun chickenFoodDao(): ChickenFoodDao

    companion object {
        @Volatile
        private var INSTANCE: ChickenFoodDatabase? = null

        fun getInstance(context: Context): ChickenFoodDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChickenFoodDatabase::class.java,
                    "cluckcheck_foods.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
