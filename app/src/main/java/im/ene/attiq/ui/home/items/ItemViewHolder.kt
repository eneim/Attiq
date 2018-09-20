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
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import attiq.binding.BindingComponent
import attiq.data.entity.Item
import im.ene.attiq.BR
import im.ene.attiq.R

/**
 * @author eneim (2018/05/07).
 */
class ItemViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    component: BindingComponent,
    private val itemClick: (View, Int) -> Unit
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.holder_simple_post, parent, false)) {

  private val binder = DataBindingUtil.bind(itemView, component) as ViewDataBinding?

  init {
    itemView.setOnClickListener { itemClick.invoke(it, adapterPosition) }
  }

  fun bind(item: Item) {
    binder?.run {
      this.setVariable(BR.item, item)
      this.executePendingBindings()
    }
  }
}
