package com.service.codingtest.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.paging.PagingData

import com.service.codingtest.model.response.DocumentData
import com.service.codingtest.network.Constant
import com.service.codingtest.network.ImageAPI
import com.service.codingtest.repository.ImageRepository
import com.service.codingtest.repository.InMemoryByItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class ImageListViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val documentRepository: ImageRepository

    companion object {
        const val KEY_SEARCH = "search"
    }

    val searchWord = ObservableField("")

    val filterList = arrayListOf<String>()
    var filterSelected = Constant.MENU_ALL

    private val clearListCh = Channel<Unit>(Channel.CONFLATED)

    init {
        documentRepository = InMemoryByItemRepository(ImageAPI.create())
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val posts = flowOf(
        clearListCh.consumeAsFlow().map {

            PagingData.empty<DocumentData>()
        },
        savedStateHandle.getLiveData<String>(KEY_SEARCH)
            .asFlow()
            .flatMapLatest {
                documentRepository.postsOfSubDocument(it, 30, filterList, filterSelected)
            }
    ).flattenMerge(2)

    fun showSubreddit() {
        clearListCh.offer(Unit)

        savedStateHandle.set(KEY_SEARCH, searchWord.get().toString())
    }
}