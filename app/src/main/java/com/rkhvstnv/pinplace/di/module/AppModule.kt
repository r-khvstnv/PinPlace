package com.rkhvstnv.pinplace.di.module


import dagger.Module

@Module(includes = [
    ViewModelBuilderModule::class,
    ViewModelModule::class,
    NetworkModule::class
])
object AppModule