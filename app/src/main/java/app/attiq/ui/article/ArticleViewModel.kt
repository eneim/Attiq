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

package app.attiq.ui.article

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import app.attiq.AttiqApp
import app.attiq.data.createApi
import app.attiq.data.entity.Item
import app.attiq.repositories.ArticleRepository

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: ArticleRepository = run {
    val api = (application as AttiqApp).retrofit.createApi()
    ArticleRepository(api)
  }

  val itemId: MutableLiveData<String> = MutableLiveData<String>()

  val itemDetail: LiveData<Item> = itemId.switchMap { id ->
    liveData {
      emit(repository.getArticle(id))
    }
  }
}
