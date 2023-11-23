package com.caspar.homeworkpixabay.di

import com.caspar.homeworkpixabay.model.ImagesRepository
import com.caspar.homeworkpixabay.model.ImagesRepositoryImply
import com.caspar.homeworkpixabay.model.RealmRepository
import com.caspar.homeworkpixabay.model.RealmRepositoryImply
import com.caspar.homeworkpixabay.model.interfaceDefine.Images
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideImagesRepository(apiService: Images): ImagesRepository {
        return ImagesRepositoryImply(apiService)
    }

    @Singleton
    @Provides
    fun provideRealmRepository(realm: Realm): RealmRepository {
        return RealmRepositoryImply(realm)
    }
}