package com.caspar.homeworkpixabay.model

import com.caspar.homeworkpixabay.model.interfaceDefine.AsDBObject
import com.caspar.homeworkpixabay.model.interfaceDefine.AsUIClass
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

interface RealmRepository {
    suspend fun <U : AsDBObject<O>, O : RealmObject> save(uiObject: U)
    suspend fun <U : AsDBObject<O>, O : RealmObject> save(list: List<U>)
    suspend fun <U, O> read(schemaClass: KClass<O>): List<U> where O : RealmObject, O : AsUIClass<U>
    suspend fun <U, O> read(
        schemaClass: KClass<O>,
        quantity: Int,
        propertyString: String,
        sort: Sort
    ): List<U> where O : RealmObject, O : AsUIClass<U>

    suspend fun <U : AsDBObject<O>, O : RealmObject> delete(target: U)
    suspend fun <U : AsDBObject<O>, O : RealmObject> delete(list: List<U>)
    suspend fun <O : RealmObject> delete(schemaClass: KClass<O>)
}

@Singleton
class RealmRepositoryImpl @Inject constructor(
    private val realm: Realm
) : RealmRepository {
    override suspend fun <U : AsDBObject<O>, O : RealmObject> save(uiObject: U) {
        realm.write {
            copyToRealm(uiObject.toObject())
        }
    }

    override suspend fun <U : AsDBObject<O>, O : RealmObject> save(list: List<U>) {
        realm.write {
            list.forEach { copyToRealm(it.toObject()) }
        }
    }

    override suspend fun <U, O> read(schemaClass: KClass<O>): List<U> where O : RealmObject, O : AsUIClass<U> {
        val resultList = mutableListOf<U>()
        realm.query(schemaClass).find().forEach { dbObj ->
            resultList.add(dbObj.toUI())
        }
        return resultList
    }

    override suspend fun <U, O> read(
        schemaClass: KClass<O>,
        quantity: Int,
        propertyString: String,
        sort: Sort,
    ): List<U> where O : RealmObject, O : AsUIClass<U> {
        val resultList = mutableListOf<U>()

        return when {
            quantity < 0 -> read(schemaClass)
            quantity == 0 -> resultList
            else -> {
                realm.query(schemaClass)
                    .sort(propertyString, sort)
                    .limit(quantity)
                    .find()
                    .forEach { dbObj -> resultList.add(dbObj.toUI()) }

                resultList
            }
        }
    }

    override suspend fun <U : AsDBObject<O>, O : RealmObject> delete(target: U) {
        realm.write {
            delete(target.toObject())
        }
    }

    override suspend fun <U : AsDBObject<O>, O : RealmObject> delete(list: List<U>) {
        realm.write {
            val realmList = realmListOf<O>()
            list.forEach { realmList.add(it.toObject()) }
            delete(realmList)
        }
    }

    override suspend fun <O : RealmObject> delete(schemaClass: KClass<O>) {
        realm.write {
            delete(this.query(schemaClass).find())
        }
    }
}