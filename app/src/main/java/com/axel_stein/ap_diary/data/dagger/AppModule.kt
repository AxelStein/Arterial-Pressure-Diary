package com.axel_stein.ap_diary.data.dagger

import android.content.Context
import androidx.room.Room
import com.axel_stein.ap_diary.data.room.AppDatabase
import com.axel_stein.ap_diary.data.room.dao.LogDao
import com.axel_stein.ap_diary.data.room.repository.LogRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, context.packageName).build()
    }

    @Provides
    @Singleton
    fun provideApLogDao(db: AppDatabase) = db.apLogDao()

    @Provides
    @Singleton
    fun providePulseLogDao(db: AppDatabase) = db.pulseLogDao()

    @Provides
    @Singleton
    fun provideLogDao(db: AppDatabase) = db.logDao()

    @Provides
    @Singleton
    fun provideRepository(db: AppDatabase, dao: LogDao) = LogRepository(context, db, dao)
}