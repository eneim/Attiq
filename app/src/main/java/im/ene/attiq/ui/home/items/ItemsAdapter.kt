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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import attiq.binding.BindingComponent
import attiq.data.entity.Item
import attiq.x.NetworkState
import im.ene.attiq.R

/**
 * @author eneim (2018/05/07).
 */
class ItemsAdapter(
    private val itemClick: (View, String) -> Unit
) : PagedListAdapter<Item, ViewHolder>(
    DIFF_CALLBACK) {

  companion object {
    const val TAG = "Attiq:Adapter"
    val DIFF_CALLBACK = object : ItemCallback<Item>() {
      override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return newItem.itemId == oldItem.itemId
      }

      override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return newItem.url == oldItem.url
      }
    }
  }

  private var inflater: LayoutInflater? = null
  private var networkState: NetworkState? = null
  private val component = BindingComponent()

  private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

  fun setNetworkState(newNetworkState: NetworkState?) {
    val previousState = this.networkState
    val hadExtraRow = hasExtraRow()
    this.networkState = newNetworkState
    val hasExtraRow = hasExtraRow()
    if (hadExtraRow != hasExtraRow) {
      if (hadExtraRow) {
        notifyItemRemoved(super.getItemCount())
      } else {
        notifyItemInserted(super.getItemCount())
      }
    } else if (hasExtraRow && previousState != newNetworkState) {
      notifyItemChanged(itemCount - 1)
    }
  }

  override fun getItemCount(): Int {
    return super.getItemCount() + if (hasExtraRow()) 1 else 0
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    if (inflater?.context != parent.context) inflater = LayoutInflater.from(parent.context)
    return when (viewType) {
      R.layout.holder_simple_post -> ItemViewHolder(inflater!!, parent,
          component
      ) { view, pos -> itemClick.invoke(view, getItem(pos)!!.itemId) }
      R.layout.holder_loading -> LoadingViewHolder(
          inflater!!.inflate(viewType, parent, false))
      else -> throw IllegalArgumentException("unknown view type $viewType")
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (getItemViewType(position) == R.layout.holder_simple_post) {
      getItem(position)?.run {
        (holder as? ItemViewHolder)?.bind(this)
      }
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (hasExtraRow() && position == itemCount - 1) {
      R.layout.holder_loading
    } else {
      R.layout.holder_simple_post
    }
  }
}
