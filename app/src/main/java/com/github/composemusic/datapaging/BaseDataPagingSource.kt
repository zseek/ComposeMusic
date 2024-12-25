package com.github.composemusic.datapaging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState

//默认加载数量
const val PAGE_SIZE: Int = 20

class BaseDataPagingSource<T : Any>(private val block: suspend (Int, Int) -> List<T>) :PagingSource<Int, T>() {

    // 在刷新时获得新的起始位置
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }

    /*
    // 加载数据
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val offset = params.key ?: 0
        val limit = params.loadSize
        return try {
            val response = block(offset, limit)
            val previousPage = if (offset > 0) offset - 1 else null //前一页
            val nextPage = offset + 1 //下一页
            LoadResult.Page(
                data = response,
                prevKey = previousPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val offset = params.key ?: 0
        val limit = params.loadSize
        return try {
            val response = block(offset, limit)
            val nextKey = if (response.isEmpty()) {
                null // 如果没有更多数据，停止加载
            } else {
                offset + limit // 下一页从当前页的结束位置开始
            }
            val prevKey = if (offset == 0) null else offset - limit // 前一页起点
            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

/**
 * 用于创建 Pager 实例
 * @param pageSize 每页加载数量
 * @param enablePlaceholders 是否启用占位符(用来显示加载状态)
 * @param block suspend函数, 用于获取数据
 * @return Pager 实例
 */
fun <T : Any> creator(
    pageSize: Int = PAGE_SIZE,
    enablePlaceholders: Boolean = false,
    block: suspend (Int, Int) -> List<T>
): Pager<Int, T> {
    return Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = enablePlaceholders),
        pagingSourceFactory = { BaseDataPagingSource(block = block) }
    )
}



