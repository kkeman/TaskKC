package com.service.codingtest.repository

import androidx.paging.PagingData
import com.service.codingtest.model.response.DocumentData
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun postsOfSubDocument(
        query: String,
        pageSize: Int,
        filterList: ArrayList<String>,
        filter: String
    ): Flow<PagingData<DocumentData>>
}