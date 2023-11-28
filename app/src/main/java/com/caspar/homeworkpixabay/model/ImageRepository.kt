package com.caspar.homeworkpixabay.model

import com.caspar.homeworkpixabay.model.dataClass.Hit
import com.caspar.homeworkpixabay.model.interfaceDefine.Images
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface ImagesRepository {
    suspend fun fetchPhotos(keyword: String, type: String, pageNumber: Int = 1, pageQuantity: Int = 30): List<Hit>?
}

@Singleton
class ImagesRepositoryImpl @Inject constructor(
    private val apiService: Images,
) : ImagesRepository {
    override suspend fun fetchPhotos(keyword: String, type: String, pageNumber: Int, pageQuantity: Int): List<Hit>? {
        val qString = keyword.replace(" ", "+").trim()
        val stringParams = mapOf(
            "q" to qString,
            "image_type" to type,
        )
        val intParams = mapOf(
            "page" to pageNumber,
            "per_page" to pageQuantity,
        )
        val response = apiService.fetchImages(stringParams, intParams)

        return when {
            !checkNetworkStatus(response) -> null
            response.body() == null -> emptyList()
            else -> response.body()!!.hits
        }
    }

    private fun <T> checkNetworkStatus(response: Response<T>): Boolean {
        return response.isSuccessful && response.errorBody() == null
    }
}