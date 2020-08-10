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

package app.attiq.data

import app.attiq.data.entity.Item
import app.attiq.data.entity.User
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QiitaApi {

  @GET("/api/v2/items")
  suspend fun items(
    @Query("page") page: Int,
    @Query("per_page") count: Int,
    @Query("query") query: String?
  ): List<Item>

  @GET("/api/v2/authenticated_user")
  suspend fun authUser(): User

  @GET("/api/v2/items/{item_id}")
  suspend fun itemDetail(@Path("item_id") itemId: String): Item

  @GET("/api/v2/users/{user_name}")
  suspend fun userDetail(@Path("user_name") userName: String): User
}

fun Retrofit.createApi(): QiitaApi = create(QiitaApi::class.java)
