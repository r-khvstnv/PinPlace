package com.rkhvstnv.pinplace.di.app

import com.rkhvstnv.pinplace.di.viewmodel.ViewModelBuilderModule
import com.rkhvstnv.pinplace.di.viewmodel.ViewModelModule
import dagger.Module

@Module(includes = [ViewModelBuilderModule::class, ViewModelModule::class])
object AppModule