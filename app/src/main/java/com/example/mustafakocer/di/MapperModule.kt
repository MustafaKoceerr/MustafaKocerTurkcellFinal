package com.example.mustafakocer.di

// ...
import com.example.mustafakocer.data.error.ErrorMapperImpl
import com.example.mustafakocer.domain.mapper.ErrorMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// ...

// Ayrı bir modül oluşturmak daha temiz olabilir.
@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {

    @Binds
    abstract fun bindErrorMapper(
        errorMapperImpl: ErrorMapperImpl,
    ): ErrorMapper
}