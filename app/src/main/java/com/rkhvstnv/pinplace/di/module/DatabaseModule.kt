package com.rkhvstnv.pinplace.di.module

import android.app.Application
import androidx.room.Room
import com.rkhvstnv.pinplace.database.PlaceDao
import com.rkhvstnv.pinplace.database.PlaceDatabase
import com.rkhvstnv.pinplace.database.PlaceRepository
import dagger.Module
import dagger.Provides


@Module
class DatabaseModule(private val application: Application) {
    @Provides
    fun providesApplication(): Application{
        return application
    }

    @Provides
    fun providesPlaceDatabase(app: Application): PlaceDatabase{
        return Room.databaseBuilder(app.applicationContext, PlaceDatabase::class.java, "place_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesPlaceDao(placeDatabase: PlaceDatabase) = placeDatabase.getPlaceDao()

    @Provides
    fun providesPlaceRepository(placeDao: PlaceDao) = PlaceRepository(placeDao)
}