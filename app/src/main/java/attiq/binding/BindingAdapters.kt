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

package attiq.binding

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.parseAsHtml
import androidx.databinding.BindingAdapter
import attiq.data.entity.Item
import attiq.data.entity.Tag
import attiq.data.entity.User
import attiq.widget.PostView
import attiq.x.Resource
import attiq.x.glide.CircleTransform
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import im.ene.attiq.R

/**
 * @author eneim (2018/09/19).
 */
class BindingAdapters {

  val options = RequestOptions().placeholder(R.mipmap.ic_launcher)

  @BindingAdapter("shouldGone")
  fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
  }

  @BindingAdapter("htmlTitle")
  fun setActivityTitle(toolbar: CollapsingToolbarLayout, item: Resource<Item>?) {
    toolbar.title = item?.data?.title?.parseAsHtml() ?: ""
  }

  @BindingAdapter("itemTitle")
  fun setItemTitle(toolbar: TextView, item: Item?) {
    toolbar.text = item?.title?.parseAsHtml() ?: ""
  }

  @BindingAdapter("tags")
  fun setTags(chips: ChipGroup, tags: List<Tag>?) {
    if (tags == null || tags.isEmpty()) chips.visibility = View.GONE
    else {
      chips.visibility = View.VISIBLE
      chips.removeAllViews()
      tags.forEachIndexed { index, tag ->
        Chip(chips.context).apply {
          this.text = tag.name
          chips.addView(this, index,
              LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        }
      }
    }
  }

  @BindingAdapter(value = ["content"])
  fun setMarkdown(view: PostView, content: Resource<Item>?) {
    content?.data?.let {
      val html = it.renderedBody ?: ""
      val title = it.title ?: ""
      if (html.isNotEmpty()) view.render(title, html)
    }
  }

  @BindingAdapter("userIcon")
  fun setItemUserIcon(view: ImageView, user: User?) {
    Glide.with(view).load(user?.profileImageUrl).apply(
        options.transform(CircleTransform.getInstance())).into(view)
  }

  @SuppressLint("SetTextI18n")
  @BindingAdapter("itemInfo")
  fun setItemInfo(view: TextView, item: Item?) {
    if (item === null) view.visibility = View.GONE
    else {
      view.visibility = View.VISIBLE
      view.text = "like: ${item.likesCount}・comment: ${item.commentsCount}"
    }
  }

  @SuppressLint("SetTextI18n")
  @BindingAdapter("itemCreation")
  fun setItemCreation(view: TextView, item: Item?) {
    if (item === null) view.visibility = View.GONE
    else {
      view.visibility = View.VISIBLE
      view.text = "${item.user.id}・${item.createdAt}"
    }
  }
}