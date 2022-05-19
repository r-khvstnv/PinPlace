package com.rkhvstnv.pinplace.di.module

import androidx.lifecycle.ViewModelProvider
import com.rkhvstnv.pinplace.di.factory.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelBuilderModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}