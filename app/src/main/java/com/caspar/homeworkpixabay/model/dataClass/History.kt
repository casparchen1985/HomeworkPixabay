package com.caspar.homeworkpixabay.model.dataClass

import android.os.Parcelable
import com.caspar.homeworkpixabay.model.interfaceDefine.AsDBObject
import com.caspar.homeworkpixabay.model.realmObject.HistoryObject
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    val unix: Long = 0L,
    val keyword: String? = null,
) : Parcelable, AsDBObject<HistoryObject> {
    override fun toObject(): HistoryObject {
        return HistoryObject(
            unix, keyword
        )
    }
}