package com.axel_stein.ap_diary.data.dagger

import android.content.Context
import androidx.room.Room
import com.axel_stein.ap_diary.data.room.AppDatabase
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
    fun provideApLogDao(db: AppDatabase) = db.apLogDao()

    @Provides
    fun providePulseLogDao(db: AppDatabase) = db.pulseLogDao()

    @Provides
    fun provideLogDao(db: AppDatabase) = db.logDao()
}