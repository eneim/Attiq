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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.attiq.data.QiitaApi
import app.attiq.data.entity.Item
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

  companion object {
    operator fun invoke(api: QiitaApi, pageSize: Int = 20): ItemsRepository =
      ItemsRepositoryImpl(api, pageSize)
  }

  fun getItems(query: String? = null): Flow<PagingData<Item>>
}

private class ItemsRepositoryImpl(
  private val api: QiitaApi,
  private val pageSize: Int = 20
) : ItemsRepository {

  @ExperimentalPagingApi
  override fun getItems(query: String?): Flow<PagingData<Item>> =
    Pager(
      PagingConfig(
        pageSize = pageSize,
        enablePlaceholders = false,
        maxSize = 100
      ),
      initialKey = 1
    ) {
      PagedKeyPagingSource(api, query)
    }
      .flow
}
