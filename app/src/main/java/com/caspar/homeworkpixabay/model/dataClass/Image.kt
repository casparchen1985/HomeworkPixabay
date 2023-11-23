package com.caspar.homeworkpixabay.model.dataClass

import android.os.Parcelable
import com.caspar.homeworkpixabay.model.interfaceDefine.AsDBObject
import com.caspar.homeworkpixabay.model.realmObject.HitObject
import com.caspar.homeworkpixabay.model.realmObject.ImageResultObject
import com.google.gson.annotations.SerializedName
import io.realm.kotlin.ext.realmListOf
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageResult(
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("totalHits")
    val totalHits: Int = 0,
    @SerializedName("hits")
    var hits: List<Hit> = emptyList(),
) : Parcelable, AsDBObject<ImageResultObject> {
    override fun toObject(): ImageResultObject {
        val realmList = realmListOf<HitObject>()
        hits.forEach { data ->
            realmList.add(data.toObject())
        }
        return ImageResultObject(
            total,
            totalHits,
            realmList
        )
    }
}

@Parcelize
data class Hit(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("pageURL")
    var pageURL: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("tags")
    var tags: String? = null,
    @SerializedName("previewURL")
    var previewURL: String? = null,
    @SerializedName("previewWidth")
    var previewWidth: Int = 0,
    @SerializedName("previewHeight")
    var previewHeight: Int = 0,
    @SerializedName("webformatURL")
    var webformatURL: String? = null,
    @SerializedName("webformatWidth")
    var webFormatWidth: Int = 0,
    @SerializedName("webformatHeight")
    var webFormatHeight: Int = 0,
    @SerializedName("largeImageURL")
    var largeImageURL: String? = null,
    @SerializedName("fullHDURL")
    var fullHDURL: String? = null,
    @SerializedName("imageURL")
    var imageURL: String? = null,
    @SerializedName("imageWidth")
    var imageWidth: Int = 0,
    @SerializedName("imageHeight")
    var imageHeight: Int = 0,
    @SerializedName("imageSize")
    var imageSize: Int = 0,
    @SerializedName("views")
    var views: Int = 0,
    @SerializedName("likes")
    var likes: Int = 0,
    @SerializedName("comments")
    var comments: Int = 0,
    @SerializedName("user_id")
    var userId: Int = 0,
    @SerializedName("user")
    var user: String? = null,
    @SerializedName("userImageURL")
    var userImageURL: String? = null,
) : Parcelable, AsDBObject<HitObject> {
    override fun toObject(): HitObject {
        return HitObject(
            id,
            pageURL,
            type,
            tags,
            previewURL,
            previewWidth,
            previewHeight,
            webformatURL,
            webFormatWidth,
            webFormatHeight,
            largeImageURL,
            fullHDURL,
            imageURL,
            imageWidth,
            imageHeight,
            imageSize,
            views,
            likes,
            comments,
            userId,
            user,
            userImageURL
        )
    }
}