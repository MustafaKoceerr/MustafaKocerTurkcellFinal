package com.example.mustafakocer.di

import com.example.mustafakocer.data.error.ErrorMapperImpl
import com.example.mustafakocer.domain.mapper.ErrorMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module responsible for binding interface abstractions to their concrete implementations.
 * Using @Binds is more efficient than @Provides for this purpose as it generates less code.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {

    /**
     * Binds the [ErrorMapper] interface to its [ErrorMapperImpl] implementation,
     * allowing other classes to depend on the abstraction rather than the concrete class.
     */
    @Binds
    abstract fun bindErrorMapper(
        errorMapperImpl: ErrorMapperImpl,
    ): ErrorMapper
}