/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
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

package im.ene.attiq.ui.home.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import attiq.data.ApiV2
import attiq.data.dao.AttiqDb
import attiq.data.dao.ItemDao
import attiq.data.dao.TagDao
import attiq.data.entity.Item
import attiq.x.ItemsBoundaryCallback
import attiq.x.LiveResource
import attiq.x.NetworkState
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named

/**
 *
 * Repository helps communicate with Data layer. It dispatches request to business logic, and
 * response to presenter layer. [ApiV2]
 *
 * @author eneim (2018/03/18).
 */
class ItemsRepository @Inject constructor(
    private val apiV2: ApiV2,
    private val db: AttiqDb,
    private val tagDao: TagDao,
    private val itemDao: ItemDao,
    @Named("io.executor") private val ioExecutor: Executor,
    @Named("disk.executor") private val diskExecutor: Executor
) {

  companion object {
    private const val DEFAULT_NETWORK_PAGE_SIZE = 20
  }

  private val networkPageSize = DEFAULT_NETWORK_PAGE_SIZE
  private val boundaryCallback = ItemsBoundaryCallback(
      apiV2,
      ioExecutor, diskExecutor,
      this::insertResultIntoDb,
      networkPageSize
  )
  private val refreshTrigger = MutableLiveData<Unit>()
  private val refreshState = Transformations.switchMap(refreshTrigger) {
    actualRefresh()
  }

  fun getItems(): LiveResource<PagedList<Item>> {
    val builder = LivePagedListBuilder<Int, Item>(itemDao.loadItemsDataSource(), networkPageSize)
        .setBoundaryCallback(boundaryCallback)

    return LiveResource(
        builder.build(),
        boundaryCallback.networkState,
        refreshState
    )
  }

  fun refresh() {
    refreshTrigger.value = null
  }

  fun retry() /* () -> Unit */ {
    boundaryCallback.helper.retryAllFailed()
  }

  private fun actualRefresh(): LiveData<NetworkState> {
    val networkState = MutableLiveData<NetworkState>()
    networkState.value = NetworkState.LOADING
    apiV2.items(1, networkPageSize, null).doOnNext {
      db.runInTransaction {
        itemDao.deleteAllCurrentItems()
        insertResultIntoDb(it.body())
      }
      networkState.postValue(NetworkState.LOADED)
    }.doOnError {
      networkState.postValue(NetworkState.error(it.message))
    }.subscribe()

    return networkState
  }

  private fun insertResultIntoDb(items: List<Item>?) {
    diskExecutor.execute {
      db.runInTransaction {
        items?.forEach {
          tagDao.insertTags(it.tags)
        }?.also {
          itemDao.insertItems(items)
        }
      }
    }
  }
}