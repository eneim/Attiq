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

@JsonClass(generateAdapter = true)
data class User(
  @Json(name = "id") val id: String,
  @Json(name = "permanent_id") val permanentId: Int,
  @Json(name = "followees_count") val followeesCount: Int,
  @Json(name = "followers_count") val followersCount: Int,
  @Json(name = "items_count") val itemsCount: Int,

  @Json(name = "profile_image_url") val profileImageUrl: String,
  @Json(name = "description") val description: String?,
  @Json(name = "name") val name: String?,
  @Json(name = "location") val location: String?,

  @Json(name = "organization") val organization: String?,
  @Json(name = "facebook_id") val facebookId: String?,
  @Json(name = "github_login_name") val githubLoginName: String?,
  @Json(name = "linkedin_id") val linkedinId: String?,
  @Json(name = "twitter_screen_name") val twitterScreenName: String?,
  @Json(name = "website_url") val websiteUrl: String?,
  @Transient val isAuthUser: Boolean = false
)
