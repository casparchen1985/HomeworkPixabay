package com.caspar.homeworkpixabay.model.realmObject

import com.caspar.homeworkpixabay.model.dataClass.Hit
import com.caspar.homeworkpixabay.model.dataClass.ImageResult
import com.caspar.homeworkpixabay.model.interfaceDefine.AsUIClass
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

open class ImageResultObject() : RealmObject, AsUIClass<ImageResult> {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var total: Int = 0
    var totalHits: Int = 0
    var hits: RealmList<HitObject> = realmListOf()

    constructor(total: Int, totalHits: Int, hits: RealmList<HitObject>) : this() {
        this.total = total
        this.totalHits = totalHits
        this.hits = hits
    }

    override fun toUI(): ImageResult {
        val hitList = mutableListOf<Hit>()
        hits.forEach { obj ->
            hitList.add(obj.toUI())
        }
        return ImageResult(
            total,
            totalHits,
            hitList
        )
    }
}

open class HitObject() : RealmObject, AsUIClass<Hit> {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var id: Int = 0
    var pageURL: String? = null
    var type: String? = null
    var tags: String? = null
    var previewURL: String? = null
    var previewWidth: Int = 0
    var previewHeight: Int = 0
    var webformatURL: String? = null
    var webFormatWidth: Int = 0
    var webFormatHeight: Int = 0
    var largeImageURL: String? = null
    var fullHDURL: String? = null
    var imageURL: String? = null
    var imageWidth: Int = 0
    var imageHeight: Int = 0
    var imageSize: Int = 0
    var views: Int = 0
    var likes: Int = 0
    var comments: Int = 0
    var userId: Int = 0
    var user: String? = null
    var userImageURL: String? = null

    constructor(
        id: Int,
        pageURL: String?,
        type: String?,
        tags: String?,
        previewURL: String?,
        previewWidth: Int,
        previewHeight: Int,
        webformatURL: String?,
        webFormatWidth: Int,
        webFormatHeight: Int,
        largeImageURL: String?,
        fullHDURL: String?,
        imageURL: String?,
        imageWidth: Int,
        imageHeight: Int,
        imageSize: Int,
        views: Int,
        likes: Int,
        comments: Int,
        userId: Int,
        user: String?,
        userImageURL: String?,
    ) : this() {
        this.id = id
        this.pageURL = pageURL
        this.type = type
        this.tags = tags
        this.previewURL = previewURL
        this.previewWidth = previewWidth
        this.previewHeight = previewHeight
        this.webformatURL = webformatURL
        this.webFormatWidth = webFormatWidth
        this.webFormatHeight = webFormatHeight
        this.largeImageURL = largeImageURL
        this.fullHDURL = fullHDURL
        this.imageURL = imageURL
        this.imageWidth = imageWidth
        this.imageHeight = imageHeight
        this.imageSize = imageSize
        this.views = views
        this.likes = likes
        this.comments = comments
        this.userId = userId
        this.user = user
        this.userImageURL = userImageURL
    }

    override fun toUI(): Hit {
        return Hit(
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