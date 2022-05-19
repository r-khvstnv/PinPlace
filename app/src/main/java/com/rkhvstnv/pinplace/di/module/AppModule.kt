package com.rkhvstnv.pinplace.di.module


import dagger.Module

@Module(includes = [
    ViewModelBuilderModule::class,
    ViewModelModule::class,
    NetworkModule::class,
    DatabaseModule::class
])
object AppModule