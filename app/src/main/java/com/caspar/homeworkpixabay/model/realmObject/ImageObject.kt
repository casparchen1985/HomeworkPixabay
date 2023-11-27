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
    private var _id: ObjectId = BsonObjectId()
    private var total: Int = 0
    private var totalHits: Int = 0
    private var hits: RealmList<HitObject> = realmListOf()

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
    private var _id: ObjectId = BsonObjectId()
    private var id: Int = 0
    private var pageURL: String? = null
    private var type: String? = null
    private var tags: String? = null
    private var previewURL: String? = null
    private var previewWidth: Int = 0
    private var previewHeight: Int = 0
    private var webformatURL: String? = null
    private var webFormatWidth: Int = 0
    private var webFormatHeight: Int = 0
    private var largeImageURL: String? = null
    private var fullHDURL: String? = null
    private var imageURL: String? = null
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var imageSize: Int = 0
    private var views: Int = 0
    private var likes: Int = 0
    private var comments: Int = 0
    private var userId: Int = 0
    private var user: String? = null
    private var userImageURL: String? = null

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