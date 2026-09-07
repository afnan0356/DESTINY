package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.DestinyDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.LifeEntity
import com.example.data.local.entity.SaveSlotEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        SaveSlotEntity::class,
        LifeEntity::class,
        CharacterEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DestinyDatabase : RoomDatabase() {

    abstract fun destinyDao(): DestinyDao

    companion object {
        @Volatile
        private var INSTANCE: DestinyDatabase? = null

        fun getDatabase(context: Context): DestinyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DestinyDatabase::class.java,
                    "destiny_master_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
