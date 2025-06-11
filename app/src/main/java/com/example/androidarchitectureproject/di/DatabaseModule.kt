package com.example.androidarchitectureproject.di

import android.content.Context
import androidx.room.Room
import com.example.androidarchitectureproject.data.local.ImageDatabase
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
    fun provideImageDatabase(@ApplicationContext context: Context): ImageDatabase {
        return Room.databaseBuilder(
            context,
            ImageDatabase::class.java,
            "images.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideImageDao(database: ImageDatabase) = database.imageDao()
} 