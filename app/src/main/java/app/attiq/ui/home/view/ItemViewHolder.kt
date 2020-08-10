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

package app.attiq.ui.home.view

import android.graphics.Bitmap
import android.text.format.DateUtils
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import app.attiq.R
import app.attiq.common.BaseViewHolder
import app.attiq.data.entity.Item
import app.attiq.databinding.HolderPostItemBinding
import coil.api.load

class ItemViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.holder_post_item) {

  private val binding = HolderPostItemBinding.bind(itemView)
  private val resources = itemView.resources

  internal var article: Item? = null

  override fun onBind(payload: Any?) {
    (payload as Item?)?.let { item ->
      article = item
      binding.itemTitle.text = item.title.parseAsHtml(flags = HtmlCompat.FROM_HTML_MODE_COMPACT)
      binding.itemCreatedAt.text = DateUtils.getRelativeTimeSpanString(item.createdAt.time)
      binding.userIcon.load(item.user.profileImageUrl) {
        crossfade(true)
        bitmapConfig(Bitmap.Config.RGB_565)
        placeholder(R.drawable.ic_baseline_face_24)
        error(R.drawable.ic_baseline_face_24)
        fallback(R.drawable.ic_baseline_face_24)
      }

      val itemLike =
        resources.getQuantityString(R.plurals.item_likes, item.likesCount, item.likesCount)
      val itemComment =
        resources.getQuantityString(R.plurals.item_comments, item.commentsCount, item.commentsCount)
      binding.itemInfo.text = resources.getString(R.string.item_info_full, itemLike, itemComment)
    }
  }

  internal fun tryRefreshTime() {
    article?.let {
      binding.itemCreatedAt.text = DateUtils.getRelativeTimeSpanString(it.createdAt.time)
    }
  }

  override fun onRecycle() {
    super.onRecycle()
    article = null
  }
}
