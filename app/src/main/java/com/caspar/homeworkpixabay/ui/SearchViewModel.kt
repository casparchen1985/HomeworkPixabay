package com.caspar.homeworkpixabay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.homeworkpixabay.model.ImagesRepository
import com.caspar.homeworkpixabay.model.RealmRepository
import com.caspar.homeworkpixabay.model.dataClass.History
import com.caspar.homeworkpixabay.model.realmObject.HistoryObject
import com.caspar.homeworkpixabay.model.realmObject.HitObject
import com.caspar.homeworkpixabay.ui.customized.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imagesRepos: ImagesRepository,
    private val realmRepos: RealmRepository,
) : ViewModel() {
    val searchResultLiveData = SingleLiveData<Boolean>()
    val searchHistoryLiveData = SingleLiveData<ArrayList<String>>()

    fun searchImages(keyword: String, type: String) {
        viewModelScope.launch {
            val remoteData = imagesRepos.fetchPhotos(keyword, type)
            realmRepos.delete(HitObject::class)
            if (!remoteData.isNullOrEmpty()) realmRepos.save(remoteData)
            searchResultLiveData.postValue(remoteData != null)
        }
    }

    fun readSearchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val arrayString = arrayListOf<String>()
            realmRepos
                .read(HistoryObject::class, 50, "unix", Sort.DESCENDING)
                .forEach { history ->
                    val keyword = history.keyword ?: return@forEach
                    if (arrayString.contains(keyword)) return@forEach
                    arrayString.add(keyword)
                }
            searchHistoryLiveData.postValue(arrayString)
        }
    }

    fun saveSearchHistory(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            realmRepos.save(
                History(System.currentTimeMillis(), keyword)
            )
        }
    }
}