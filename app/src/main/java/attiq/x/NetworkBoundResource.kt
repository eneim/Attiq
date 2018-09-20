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

/**
 * @author eneim (2018/04/23).
 */

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import attiq.x.base.Skedulers
import io.reactivex.Completable
import retrofit2.Response

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 */
abstract class NetworkBoundResource<ENTITY, RAW> {

  private val result = MediatorLiveData<Resource<ENTITY>>()

  init {
    result.value = Resource.loading(result.value?.data)
    @Suppress("LeakingThis")
    val dbSource = loadFromDb()
    result.addSource(dbSource) {
      result.removeSource(dbSource)
      if (shouldFetch(it)) {
        fetchFromNetwork(dbSource)
      } else {
        result.addSource(dbSource) { newData -> setValue(
            Resource.success(newData)) }
      }
    }
  }

  @MainThread
  private fun setValue(newValue: Resource<ENTITY>) {
    if (result.value != newValue) {
      result.value = newValue
    }
  }

  private fun fetchFromNetwork(dbSource: LiveData<ENTITY>) {
    val apiResponse = fetchFromApi()
    // we re-attach dbSource as a new source, it will dispatch its latest value quickly
    result.addSource(dbSource) { newData -> setValue(Resource.loading(newData)) }
    result.addSource(apiResponse) {
      result.removeSource(apiResponse)
      result.removeSource(dbSource)
      if (it!!.isSuccessful) {
        Completable.fromAction { saveCallResult(processResponse(it)!!) }
            .subscribeOn(Skedulers.disk)
            .observeOn(Skedulers.main)
            .andThen(
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                Completable.fromAction {
                  result.addSource(loadFromDb()) { newData -> setValue(
                      Resource.success(newData)) }
                })
            .subscribe()
      } else {
        onFetchFailed()
        result.addSource(dbSource) { newData -> setValue(
            Resource.error(it.message(), newData)) }
      }
    }
  }

  protected open fun onFetchFailed() {}

  fun asLiveData(): LiveData<Resource<ENTITY>> {
    return result
  }

  @Suppress("MemberVisibilityCanBePrivate")
  @WorkerThread
  protected fun processResponse(response: Response<RAW>): RAW? {
    return response.body()
  }

  @WorkerThread
  protected abstract fun saveCallResult(item: RAW)

  @MainThread
  protected abstract fun shouldFetch(data: ENTITY?): Boolean

  @MainThread
  protected abstract fun loadFromDb(): LiveData<ENTITY>

  @MainThread
  protected abstract fun fetchFromApi(): LiveData<Response<RAW>>
}
