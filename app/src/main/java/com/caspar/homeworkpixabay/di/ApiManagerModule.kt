package com.caspar.homeworkpixabay.di

import com.caspar.homeworkpixabay.model.ApiManager
import com.caspar.homeworkpixabay.model.interfaceDefine.Images
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiManagerModule {

    @Singleton
    @Provides
    fun provideImagesService(): Images {
        return ApiManager.retrofit!!.create<Images>()
    }
}