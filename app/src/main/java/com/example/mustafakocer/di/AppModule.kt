package com.example.mustafakocer.di

import android.content.Context
import androidx.room.Room
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.db.DatabaseConstants
import com.example.mustafakocer.data.db.ProductDao
import com.example.mustafakocer.data.db.UserDao
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.preferences.UserPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.DatabaseReference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.mustafakocer.BuildConfig


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // YENİ FONKSİYON: Sadece debug build'lerde loglama yapacak olan interceptor'ı sağlar.
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY // Debug'da tüm request/response'u logla
            } else {
                HttpLoggingInterceptor.Level.NONE // Release'de hiçbir şeyi loglama
            }
        }
    }

    // YENİ FONKSİYON: Interceptor'ı içeren özel OkHttpClient'ı sağlar.
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // YENİ FONKSİYON: Retrofit instance'ını sağlar. Artık kendi OkHttpClient'ımızı kullanıyor.
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(okHttpClient) // DEĞİŞTİ: Varsayılan client yerine kendi client'ımızı verdik.
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // DEĞİŞTİ: Bu fonksiyon artık Retrofit'i oluşturmuyor, sadece hazır alıp API'yi yaratıyor.
    @Provides
    @Singleton
    fun provideIDummyApi(retrofit: Retrofit): IDummyApi {
        return retrofit.create(IDummyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Şimdilik kolaylık olması için
            .build()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.createUserDao()
    }

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao {
        return db.createProductDao()
    }

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): DatabaseReference =
        Firebase.database.reference

}