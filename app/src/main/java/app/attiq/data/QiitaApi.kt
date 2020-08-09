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
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface QiitaApi {

  @GET("/api/v2/items")
  suspend fun items(
    @Query("page") page: Int,
    @Query("per_page") count: Int,
    @Query("query") query: String?
  ): List<Item>
}

fun Retrofit.createApi(): QiitaApi = create(QiitaApi::class.java)
