package com.example.mustafakocer.di

import com.example.mustafakocer.data.network.ICategoryApi
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.network.IUsersApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // todo neden singleton yaptın tekrar bir göz at
object AppModule {

    @Singleton
    @Provides
    fun provideIDummyApi(): IDummyApi{
        return IDummyApi.invoke()
    }

    @Singleton
    @Provides
    fun provideIUserApi(): IUsersApi{
        return IUsersApi.invoke()
    }

    @Singleton
    @Provides
    fun provideICategoryApi(): ICategoryApi{
        return ICategoryApi.invoke()
    }

}