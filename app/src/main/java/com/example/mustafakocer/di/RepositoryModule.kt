package com.example.mustafakocer.di

import com.example.mustafakocer.data.repository.AuthRepositoryImpl
import com.example.mustafakocer.data.repository.CartRepositoryImpl // YENİ IMPORT
import com.example.mustafakocer.data.repository.OrderRepositoryImpl
import com.example.mustafakocer.data.repository.ProductRepositoryImpl
import com.example.mustafakocer.data.repository.UserRepositoryImpl // YENİ IMPORT
import com.example.mustafakocer.domain.repository.AuthRepository
import com.example.mustafakocer.domain.repository.CartRepository // YENİ IMPORT
import com.example.mustafakocer.domain.repository.OrderRepository
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    // YENİ EKLENDİ: UserRepository için bağlama
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    // YENİ EKLENDİ: CartRepository için bağlama
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    // DÜZELTİLDİ: AuthRepository için yorum satırları kaldırıldı.
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}