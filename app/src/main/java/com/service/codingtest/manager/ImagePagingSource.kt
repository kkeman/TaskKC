package com.service.codingtest.manager

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
import com.service.codingtest.model.response.DocumentData
import com.service.codingtest.network.Constant
import com.service.codingtest.network.ImageAPI
import retrofit2.HttpException
import java.io.IOException


class ImagePagingSource(
    private val httpClient: ImageAPI,
    private val query: String,
    private val filterList: ArrayList<String>,
    private val filter: String
) : PagingSource<Int, DocumentData>() {

    private val initialPageIndex: Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DocumentData> {
        val position = params.key ?: initialPageIndex
        return try {
            var items = httpClient.getAPI(query = query, page = position).documents

            if (filter == Constant.MENU_ALL) {

                for (item in items)
                    filterList.apply {
                        if (!contains(item.collection))
                            add(item.collection)
                    }

            } else
                items = items.filter { data -> data.collection == filter }

            Page(
                data = items,
                prevKey = if (position == initialPageIndex) null else position - 1,
                nextKey = if (items.isEmpty()) null else position + 1
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}