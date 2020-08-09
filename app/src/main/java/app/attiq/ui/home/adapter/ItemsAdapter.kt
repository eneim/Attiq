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

package app.attiq.ui.home.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import app.attiq.data.entity.Item
import app.attiq.ui.home.view.ItemViewHolder

class ItemsAdapter : PagingDataAdapter<Item, ItemViewHolder>(diffCallback = ITEM_COMPARATOR) {

  private companion object {
    val ITEM_COMPARATOR: DiffUtil.ItemCallback<Item> = object : DiffUtil.ItemCallback<Item>() {
      override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
        oldItem.itemId == newItem.itemId

      override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
        oldItem.createdAt.time == newItem.createdAt.time &&
            oldItem.updatedAt.time == newItem.updatedAt.time
    }
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.onBind(getItem(position))
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
    ItemViewHolder(parent)
}
