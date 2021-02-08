package com.axel_stein.ap_diary.data.dagger

import android.content.Context
import androidx.room.Room
import com.axel_stein.ap_diary.data.AppResources
import com.axel_stein.ap_diary.data.AppSettings
import com.axel_stein.ap_diary.data.google_drive.GoogleDriveService
import com.axel_stein.ap_diary.data.room.AppDatabase
import com.axel_stein.ap_diary.data.room.dao.LogDao
import com.axel_stein.ap_diary.data.room.repository.LogRepository
import com.google.gson.*
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {
    private val appSettings = AppSettings(context)

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

    @Provides
    @Singleton
    fun provideAppResources() = AppResources(context)

    @Provides
    @Singleton
    fun provideDriveService() = GoogleDriveService(context)

    @Provides
    @Singleton
    fun provideSettings() = appSettings

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                DateTime::class.java,
                JsonSerializer {
                        src: DateTime, _: Type?, _: JsonSerializationContext? -> JsonPrimitive(src.toString())
                } as JsonSerializer<DateTime>
            )
            .registerTypeAdapter(
                DateTime::class.java,
                JsonDeserializer {
                        json: JsonElement, _: Type?, _: JsonDeserializationContext? -> DateTime(json.asString)
                } as JsonDeserializer<DateTime>
            )
            .create()
    }
}