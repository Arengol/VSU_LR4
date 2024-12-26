package ru.vsu.vladimir.vsu_lr4.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 1,
    entities = [BookEntity::class,
    StatusEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun BookDao(): BookDao
    abstract fun StatusDao(): StatusDao

}