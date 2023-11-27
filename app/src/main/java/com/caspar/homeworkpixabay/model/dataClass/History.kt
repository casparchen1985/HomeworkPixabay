package com.caspar.homeworkpixabay.model.dataClass

import com.caspar.homeworkpixabay.model.interfaceDefine.AsDBObject
import com.caspar.homeworkpixabay.model.realmObject.HistoryObject

data class History(
    val unix: Long = 0L,
    val keyword: String? = null,
) : AsDBObject<HistoryObject> {
    override fun toObject(): HistoryObject {
        return HistoryObject(
            unix, keyword
        )
    }
}