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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagingData
import app.attiq.AttiqApp
import app.attiq.data.createApi
import app.attiq.data.entity.Item
import app.attiq.repositories.ItemsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

class HomeViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: ItemsRepository = run {
    val api = (application as AttiqApp).retrofit.createApi()
    ItemsRepository(api, 25)
  }

  @FlowPreview
  val items: Flow<PagingData<Item>> = repository.getItems()
}