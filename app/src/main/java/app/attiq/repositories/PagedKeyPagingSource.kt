/*
 * Copyright (c) 2020 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.attiq.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Append
import androidx.paging.PagingSource.LoadParams.Prepend
import androidx.paging.PagingSource.LoadResult.Page
import app.attiq.data.QiitaApi
import app.attiq.data.entity.Item

class PagedKeyPagingSource(
  private val api: QiitaApi,
  private val query: String?
) : PagingSource<Int, Item>() {

  private companion object {
    const val DEFAULT_KEY = 1
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> = try {
    val pageKey = when (params) {
      is Append -> params.key
      is Prepend -> params.key
      else -> DEFAULT_KEY
    }

    Log.i("Attiq", "load start: ${params.key}, ${params.loadSize}")
    val items = api.items(pageKey, params.loadSize, query)
    Log.d("Attiq", "load done: ${params.key}, ${params.loadSize} -> ${items.size}")

    Page(
      data = items,
      prevKey = if (pageKey > 1) pageKey - 1 else null,
      nextKey = pageKey + 1
    )
  } catch (error: Exception) {
    Log.e("Attiq", "load error: ${params.key}, ${params.loadSize} -> $error")
    LoadResult.Error(error)
  }
}
