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

package im.ene.attiq.ui.home.items

import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * @author eneim (2018/05/07).
 */
class ItemsViewModel @Inject constructor(
    private val repo: ItemsRepository
) : ViewModel() {

  val posts = repo.getItems()

  fun refresh() {
    repo.refresh()
  }

  fun retry() {
    repo.retry()
  }
}