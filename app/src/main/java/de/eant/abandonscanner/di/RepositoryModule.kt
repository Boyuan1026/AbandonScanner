package de.eant.abandonscanner.di

import de.eant.abandonscanner.data.repository.QRCodeRepositoryImpl
import de.eant.abandonscanner.domain.repository.QRCodeRepository
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
    abstract fun bindQRCodeRepository(
        qrCodeRepositoryImpl: QRCodeRepositoryImpl
    ): QRCodeRepository
} 