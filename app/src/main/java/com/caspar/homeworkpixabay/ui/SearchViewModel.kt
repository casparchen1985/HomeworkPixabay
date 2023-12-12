package com.caspar.homeworkpixabay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.homeworkpixabay.model.ImagesRepository
import com.caspar.homeworkpixabay.model.RealmRepository
import com.caspar.homeworkpixabay.model.dataClass.History
import com.caspar.homeworkpixabay.model.realmObject.HistoryObject
import com.caspar.homeworkpixabay.model.realmObject.HitObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imagesRepos: ImagesRepository,
    private val realmRepos: RealmRepository,
) : ViewModel() {
    private val _searchResultSharedFlow = MutableSharedFlow<Boolean>()
    val searchResultSharedFlow = _searchResultSharedFlow.asSharedFlow()
    private val _searchHistorySharedFlow = MutableSharedFlow<ArrayList<String>>()
    val searchHistorySharedFlow = _searchHistorySharedFlow.asSharedFlow()

    fun searchImages(keyword: String, type: String) {
        viewModelScope.launch {
            val remoteData = imagesRepos.fetchPhotos(keyword, type)
            realmRepos.delete(HitObject::class)
            if (!remoteData.isNullOrEmpty()) realmRepos.save(remoteData)
            _searchResultSharedFlow.emit(remoteData != null)
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
            _searchHistorySharedFlow.emit(arrayString)
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