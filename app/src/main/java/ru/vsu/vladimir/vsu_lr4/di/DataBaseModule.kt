package ru.vsu.vladimir.vsu_lr4.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.vsu.vladimir.vsu_lr4.data.AppDatabase
import ru.vsu.vladimir.vsu_lr4.data.BookDao
import ru.vsu.vladimir.vsu_lr4.data.DataBaseContentProvider
import ru.vsu.vladimir.vsu_lr4.data.StatusDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    private val NAME = "vsu_LR4.db"

    @Provides
    @Singleton
    fun injectDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context = context, klass = AppDatabase::class.java, NAME
    ).build()

    @Provides
    @Singleton
    fun injectBookDAO(db: AppDatabase) = db.BookDao()

    @Provides
    @Singleton
    fun injectStatusDAO(db: AppDatabase) = db.StatusDao()
}