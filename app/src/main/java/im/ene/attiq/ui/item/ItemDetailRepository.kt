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

package im.ene.attiq.ui.item

import androidx.lifecycle.LiveData
import attiq.x.base.toLiveData
import attiq.data.ApiV2
import attiq.data.dao.AttiqDb
import attiq.data.dao.ItemDao
import attiq.data.dao.TagDao
import attiq.data.entity.Item
import attiq.data.entity.ItemTagJoin
import attiq.data.entity.Tag
import attiq.x.NetworkBoundResource
import attiq.x.RateLimiter
import attiq.x.Resource
import retrofit2.Response
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author eneim (2018/03/30).
 */
@Singleton
class ItemDetailRepository @Inject constructor(
    private val apiV2: ApiV2,
    private val db: AttiqDb,
    private val tagDao: TagDao,
    private val itemDao: ItemDao
) {

  val repoListRateLimit = RateLimiter<String>(10, MINUTES)

  fun getItemDetail(id: String): LiveData<Resource<Item>>? {
    return object : NetworkBoundResource<Item, Item>() {
      override fun saveCallResult(item: Item) {
        db.runInTransaction {
          itemDao.insertItem(item)
          if (item.tags != null) {
            tagDao.insertTags(item.tags)
            item.tags.map {
              ItemTagJoin(item.itemId, it.name)
            }.toList().run { itemDao.insertItemTagJoins(this) }
          }
        }
      }

      override fun shouldFetch(data: Item?): Boolean {
        return data == null || repoListRateLimit.shouldFetch(id)
      }

      override fun loadFromDb(): LiveData<Item> {
        return itemDao.load(id)
      }

      override fun fetchFromApi(): LiveData<Response<Item>> {
        return apiV2.itemDetail(id).toLiveData()
      }

      override fun onFetchFailed() {
        repoListRateLimit.reset(id)
      }
    }.asLiveData()
  }

  fun getTagsForItem(id: String): LiveData<List<Tag>> {
    return itemDao.getTagsForItem(id)
  }
}