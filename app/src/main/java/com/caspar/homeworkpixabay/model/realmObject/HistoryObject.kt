package com.caspar.homeworkpixabay.model.realmObject

import com.caspar.homeworkpixabay.model.dataClass.History
import com.caspar.homeworkpixabay.model.interfaceDefine.AsUIClass
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

open class HistoryObject() : RealmObject, AsUIClass<History> {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var unix: Long = 0L

    @Index
    var keyword: String? = null

    constructor(unix: Long, keyword: String?) : this() {
        this.unix = unix
        this.keyword = keyword
    }

    override fun toUI(): History {
        return History(
            unix, keyword
        )
    }
}