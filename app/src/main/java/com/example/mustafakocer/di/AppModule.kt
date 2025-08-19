package com.example.mustafakocer.di

import android.content.Context
import androidx.room.Room
import com.example.mustafakocer.BuildConfig
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.db.DatabaseConstants
import com.example.mustafakocer.data.db.UserDao
import com.example.mustafakocer.data.network.DummyApi
import com.example.mustafakocer.data.network.util.Authenticated
import com.example.mustafakocer.data.preferences.SessionManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Invocation
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module that provides singleton-scoped dependencies for the entire application.
 * This includes the network layer, database, and other core components.
 * Note: Some dependencies like `UserPreferences` are not provided here because they use
 * `@Inject constructor`, allowing Hilt to create them automatically.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides an [HttpLoggingInterceptor] that logs network traffic only in DEBUG builds.
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides a custom [Interceptor] that adds an "Authorization" header to requests
     * annotated with [@Authenticated]. It reads the token from the in-memory cache for performance.
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()

            val invocation = request.tag(Invocation::class.java)
            val isAuthenticated =
                invocation?.method()?.isAnnotationPresent(Authenticated::class.java) ?: false

            if (isAuthenticated) {
                sessionManager.authToken.value?.let { token ->
                    builder.header("Authorization", "Bearer $token")
                }
            }
            chain.proceed(builder.build())
        }
    }

    /**
     * Provides the application's [OkHttpClient], configured with logging and authentication interceptors.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    /**
     * Provides the [Retrofit] instance, configured with the custom [OkHttpClient] and
     * kotlinx.serialization converter.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * Provides the implementation of the [DummyApi] service interface.
     */
    @Provides
    @Singleton
    fun provideDummyApi(retrofit: Retrofit): DummyApi {
        return retrofit.create(DummyApi::class.java)
    }

    /**
     * Provides the singleton instance of the Room [AppDatabase].
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        )
            .build()
    }

    /**
     * Provides the [UserDao] instance from the app database.
     * This is not a singleton, as DAOs are lightweight and created on demand.
     */
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.createUserDao()
    }

    /**
     * Provides the root [DatabaseReference] for Firebase Realtime Database.
     */
    @Provides
    @Singleton
    fun provideRealtimeDatabase(): DatabaseReference =
        Firebase.database.reference
}