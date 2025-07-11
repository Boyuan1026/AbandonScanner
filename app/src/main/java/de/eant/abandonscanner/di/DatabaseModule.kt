package de.eant.abandonscanner.di

import android.content.Context
import androidx.room.Room
import de.eant.abandonscanner.data.local.AppDatabase
import de.eant.abandonscanner.data.local.QRCodeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideQRCodeDao(database: AppDatabase): QRCodeDao {
        return database.qrCodeDao()
    }
} 