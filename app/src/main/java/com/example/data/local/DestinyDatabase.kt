package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = false
)
abstract class DestinyDatabase : RoomDatabase() {

    abstract fun destinyDao(): DestinyDao

    companion object {
        @Volatile
        private var INSTANCE: DestinyDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE characters ADD COLUMN fertility INTEGER NOT NULL DEFAULT 80")
                db.execSQL("ALTER TABLE characters ADD COLUMN energy INTEGER NOT NULL DEFAULT 100")
                db.execSQL("ALTER TABLE characters ADD COLUMN athleticPerformance INTEGER NOT NULL DEFAULT 50")
                db.execSQL("ALTER TABLE characters ADD COLUMN gender TEXT NOT NULL DEFAULT 'Male'")
                db.execSQL("ALTER TABLE characters ADD COLUMN sexuality TEXT NOT NULL DEFAULT 'Heterosexual'")
                db.execSQL("ALTER TABLE characters ADD COLUMN talent TEXT NOT NULL DEFAULT 'None'")
                db.execSQL("ALTER TABLE characters ADD COLUMN eyeStyle TEXT NOT NULL DEFAULT 'Almond'")
                db.execSQL("ALTER TABLE characters ADD COLUMN eyeColor TEXT NOT NULL DEFAULT 'Brown'")
                db.execSQL("ALTER TABLE characters ADD COLUMN skinTone TEXT NOT NULL DEFAULT 'Fair'")
                db.execSQL("ALTER TABLE characters ADD COLUMN browStyle TEXT NOT NULL DEFAULT 'Straight'")
                db.execSQL("ALTER TABLE characters ADD COLUMN facialHairStyle TEXT NOT NULL DEFAULT 'Clean Shaven'")
                db.execSQL("ALTER TABLE characters ADD COLUMN facialHairColor TEXT NOT NULL DEFAULT 'Black'")
                db.execSQL("ALTER TABLE characters ADD COLUMN hairStyle TEXT NOT NULL DEFAULT 'Short Crop'")
                db.execSQL("ALTER TABLE characters ADD COLUMN hairColor TEXT NOT NULL DEFAULT 'Black'")
                db.execSQL("ALTER TABLE characters ADD COLUMN geneticHealthModifier REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE characters ADD COLUMN geneticIntelligenceModifier REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE characters ADD COLUMN geneticLooksModifier REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE characters ADD COLUMN birthCity TEXT NOT NULL DEFAULT 'New York'")
                db.execSQL("ALTER TABLE characters ADD COLUMN birthCountry TEXT NOT NULL DEFAULT 'United States'")
            }
        }

        fun getDatabase(context: Context): DestinyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DestinyDatabase::class.java,
                    "destiny_master_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
