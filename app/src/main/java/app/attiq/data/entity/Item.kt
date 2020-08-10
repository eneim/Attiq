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
  @Json(name = "id") val itemId: String,
  @Json(name = "title") val title: String,
  @Json(name = "user") val user: User,
  @Json(name = "body") val body: String,
  @Json(name = "rendered_body") val renderedBody: String,
  @Json(name = "coediting") val coediting: Boolean,
  @Json(name = "comments_count") val commentsCount: Int,
  @Json(name = "created_at") val createdAt: Date,
  @Json(name = "group") val group: Group?,
  @Json(name = "likes_count") val likesCount: Int,
  @Json(name = "private") val private: Boolean,
  @Json(name = "reactions_count") val reactionsCount: Int,
  @Json(name = "tags") val tags: List<Tag>,
  @Json(name = "updated_at") val updatedAt: Date,
  @Json(name = "url") val url: String,
  @Json(name = "page_views_count") val pageViewsCount: Int?
)
