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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import attiq.data.entity.Item
import attiq.data.entity.Tag
import attiq.x.AbsentLiveData
import attiq.x.Resource
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * @author eneim (2018/04/22).
 */
class ItemDetailViewModel @Inject constructor(repo: ItemDetailRepository) : ViewModel() {
  private val disposables = CompositeDisposable()
  private val itemId = MutableLiveData<String>()

  val item: LiveData<Resource<Item>>
  val tags: LiveData<List<Tag>>

  init {
    item = Transformations.switchMap(itemId) { input ->
      if (input.isEmpty()) throw IllegalArgumentException("Expect non empty Id, get: $input")
      else repo.getItemDetail(input)
    }

    tags = Transformations.switchMap(item) { input ->
      if (input.data == null) AbsentLiveData.create()
      else repo.getTagsForItem(input.data.itemId)
    }
  }

  fun setItemId(_itemId: String) {
    if (_itemId == itemId.value) return
    itemId.value = _itemId
  }

  override fun onCleared() {
    super.onCleared()
    disposables.clear()
  }
}