package com.caspar.homeworkpixabay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.homeworkpixabay.model.ImagesRepository
import com.caspar.homeworkpixabay.model.RealmRepository
import com.caspar.homeworkpixabay.model.dataClass.Hit
import com.caspar.homeworkpixabay.model.realmObject.HitObject
import com.caspar.homeworkpixabay.ui.customized.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val imagesRepository: ImagesRepository,
    private val realmRepos: RealmRepository,
) : ViewModel() {
    val fetchMoreLiveData = SingleLiveData<Boolean>()
    val imageContentLiveData = SingleLiveData<List<Hit>>()
    private var pageNumber = 2
    private val itemQuantity = 10
    private var isLocked = AtomicBoolean(false)

    fun readImagesData() {
        viewModelScope.launch {
            imageContentLiveData.postValue(realmRepos.read(HitObject::class))
        }
    }

    fun fetchMoreImages(keyword: String, type: String) {
        if (isLocked.get()) return

        viewModelScope.launch {
            isLocked.set(true)
            val remoteData = imagesRepository.fetchPhotos(keyword, type, pageNumber, itemQuantity)
            if (!remoteData.isNullOrEmpty()) {
                realmRepos.save(remoteData)
                pageNumber += 1
            }
            fetchMoreLiveData.postValue(remoteData != null)
            isLocked.set(false)
        }
    }
}