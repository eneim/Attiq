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

package app.attiq.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Item(
  @Json(name = "id") var itemId: String,
  @Json(name = "title") var title: String,
  @Json(name = "user") var user: User,
  @Json(name = "body") var body: String,
  @Json(name = "rendered_body") var renderedBody: String,
  @Json(name = "coediting") var coediting: Boolean,
  @Json(name = "comments_count") var commentsCount: Int,
  @Json(name = "created_at") var createdAt: Date,
  @Json(name = "group") var group: Group?,
  @Json(name = "likes_count") var likesCount: Int,
  @Json(name = "private") var private: Boolean,
  @Json(name = "reactions_count") var reactionsCount: Int,
  @Json(name = "tags") var tags: List<Tag>,
  @Json(name = "updated_at") var updatedAt: Date,
  @Json(name = "url") var url: String,
  @Json(name = "page_views_count") var pageViewsCount: Int?
)
