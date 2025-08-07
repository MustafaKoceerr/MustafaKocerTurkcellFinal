package com.example.mustafakocer.di

import android.content.Context
import androidx.room.Room
import com.example.mustafakocer.data.preferences.UserPreferences
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.repository.AuthRepositoryImpl
import com.example.mustafakocer.data.repository.DatabaseRepository
import com.example.mustafakocer.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // todo neden singleton yaptın tekrar bir göz at
object AppModule {

    // singleton diyor:
    /*
    singleton nesneleri yönetmek için kullanılıyor,
    nesneleri de diğer yerlere ejdet etmek için kullanılıyor
    sen bunu bir defa üret, lifecycle'ın ölmeden yok olmuyor, istediğin yerlerde tek bu nesneyi kullan

    // her api isteği atınca ayrı ayrı IDummy api oluşturmak yerine bir tane nesne oluştur, onu kullan
     */
    @Singleton
    @Provides
    fun provideIDummyApi(): IDummyApi {
        return IDummyApi.invoke()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideDatabaseRepository(
        db: AppDatabase,
        preferences: UserPreferences,
    ): DatabaseRepository {
        return DatabaseRepository(db, preferences)
    }

}