package com.service.codingtest.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.service.codingtest.manager.ImagePagingSource
import com.service.codingtest.network.ImageAPI

class InMemoryByItemRepository(private val httpClient: ImageAPI) : ImageRepository {
    override fun postsOfSubDocument(
        query: String,
        pageSize: Int,
        filterList: ArrayList<String>,
        filter: String
    ) = Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false)) {
        ImagePagingSource(
            httpClient = httpClient,
            query = query,
            filterList = filterList,
            filter = filter
        )
    }.flow
}
