/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.di.module


import dagger.Module

@Module(includes = [
    ViewModelBuilderModule::class,
    ViewModelModule::class,
    NetworkModule::class,
    DatabaseModule::class
])
object AppModule