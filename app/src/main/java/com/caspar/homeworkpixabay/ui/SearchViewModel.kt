package com.caspar.homeworkpixabay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.homeworkpixabay.model.ImagesRepository
import com.caspar.homeworkpixabay.model.RealmRepository
import com.caspar.homeworkpixabay.model.realmObject.HitObject
import com.caspar.homeworkpixabay.ui.customized.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imagesRepos: ImagesRepository,
    private val realmRepos: RealmRepository,
) : ViewModel() {
    val searchResultLiveData = SingleLiveData<Boolean>()
    private var keywordCached: String? = null
    val keyword get() = keywordCached

    fun searchImages(keyword: String, type: String) {
        viewModelScope.launch {
            val remoteData = imagesRepos.fetchPhotos(keyword, type)
            realmRepos.delete(HitObject::class)
            if (!remoteData.isNullOrEmpty()) {
                keywordCached = keyword
                realmRepos.save(remoteData)
            }
            searchResultLiveData.postValue(remoteData != null)
        }
    }
}