package com.caspar.homeworkpixabay.model.interfaceDefine

import com.caspar.homeworkpixabay.model.dataClass.ImageResult
import io.realm.kotlin.types.RealmObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

// for UI and DB object convert
interface AsDBObject<Object : RealmObject> {
    fun toObject(): Object
}

interface AsUIClass<Domain> {
    fun toUI(): Domain
}

// for remote API
interface Images {
    @GET(".")
    suspend fun fetchImages(@QueryMap string: Map<String, String>, @QueryMap int: Map<String, Int>): Response<ImageResult>
}