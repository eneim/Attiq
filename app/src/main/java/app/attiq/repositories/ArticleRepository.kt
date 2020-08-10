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

package app.attiq.repositories

import app.attiq.data.QiitaApi
import app.attiq.data.entity.Item

interface ArticleRepository {

  companion object {
    operator fun invoke(api: QiitaApi): ArticleRepository =
      ArticleRepositoryImpl(api)
  }

  suspend fun getArticle(itemId: String): Item
}

private class ArticleRepositoryImpl(private val api: QiitaApi) :
  ArticleRepository {

  override suspend fun getArticle(itemId: String): Item = api.itemDetail(itemId)
}
