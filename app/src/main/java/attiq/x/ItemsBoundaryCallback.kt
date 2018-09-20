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

package attiq.x

import androidx.paging.PagingRequestHelper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import attiq.data.ApiV2
import attiq.data.entity.Item
import attiq.x.Resource.Status
import attiq.x.Resource.Status.ERROR
import attiq.x.Resource.Status.SUCCESS
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author eneim (2018/05/07).
 */

data class NetworkState constructor(
    val status: Status,
    val msg: String? = null /* error message */) {
  companion object {
    val LOADED = NetworkState(SUCCESS)
    val LOADING = NetworkState(Status.LOADING)
    fun error(msg: String?) = NetworkState(ERROR, msg)
  }
}

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
  return PagingRequestHelper.RequestType.values().mapNotNull {
    report.getErrorFor(it)?.message
  }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
  val liveData = MutableLiveData<NetworkState>()
  addListener { report ->
    when {
      report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
      report.hasError() -> liveData.postValue(
          NetworkState.error(getErrorMessage(report)))
      else -> liveData.postValue(NetworkState.LOADED)
    }
  }
  return liveData
}

@Suppress("CanBeParameter")
class ItemsBoundaryCallback(
    private val apiV2: ApiV2,
    private val ioExecutor: Executor,
    private val diskExecutor: Executor,
    private val handleResponse: (List<Item>?) -> Unit,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<Item>() {

  val helper = PagingRequestHelper(ioExecutor)
  val networkState = helper.createStatusLiveData()
  private val pageNumber = AtomicInteger(1)

  /**
   * every time it gets new items, boundary callback simply inserts them into the database and
   * paging library takes care of refreshing the list if necessary.
   */
  private fun insertItemsIntoDb(response: Response<List<Item>>,
      callback: PagingRequestHelper.Request.Callback) {
    diskExecutor.execute {
      handleResponse(response.body())
      callback.recordSuccess()
    }
  }

  /**
   * Database returned 0 items. We should query the backend for more items.
   */
  @MainThread
  override fun onZeroItemsLoaded() {
    pageNumber.set(1)
    helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
      apiV2.items(page = pageNumber.get(), count = networkPageSize, query = null)  //
          .doOnNext { response ->
            insertItemsIntoDb(response, it)
            pageNumber.incrementAndGet()
          } //
          .doOnError { error -> it.recordFailure(error) } //
          .subscribe()
    }
  }

  /**
   * User reached to the end of the list.
   */
  @MainThread
  override fun onItemAtEndLoaded(item: Item) {
    helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
      apiV2.items(pageNumber.get(), networkPageSize, null)
          .doOnNext { response ->
            insertItemsIntoDb(response, it)
            pageNumber.incrementAndGet()
          } //
          .doOnError { error -> it.recordFailure(error) } //
          .subscribe()
    }
  }

  override fun onItemAtFrontLoaded(itemAtFront: Item) {
    // ignored, since we only ever append to what's in the DB
  }
}