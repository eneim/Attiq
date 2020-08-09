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

package app.attiq.ui.home

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import app.attiq.BuildConfig
import app.attiq.data.createApi
import app.attiq.data.entity.Item
import app.attiq.repositories.ItemsRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date

class HomeViewModel : ViewModel() {

  private val repository: ItemsRepository = run {
    val client = OkHttpClient.Builder()
      .also { if (BuildConfig.DEBUG) it.addInterceptor(HttpLoggingInterceptor()) }
      .build()

    val moshi = Moshi.Builder()
      .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
      .build()

    val retrofit = Retrofit.Builder()
      .baseUrl("https://qiita.com")
      .callFactory(client)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val api = retrofit.createApi()
    ItemsRepository(api, 25)
  }

  @FlowPreview
  val items: Flow<PagingData<Item>> = repository.getItems()
}
