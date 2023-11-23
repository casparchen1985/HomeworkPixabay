package com.caspar.homeworkpixabay.di

import com.caspar.homeworkpixabay.model.realmObject.HitObject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides
    fun provideRealm(): Realm {
        val realmConfig = RealmConfiguration
            .Builder(schema = setOf(HitObject::class))
            .compactOnLaunch()
            .deleteRealmIfMigrationNeeded()
            .name("HomeworkPixabay")
            .build()
        return Realm.open(realmConfig)
    }
}