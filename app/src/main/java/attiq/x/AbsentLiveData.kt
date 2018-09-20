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

package attiq.x

import androidx.lifecycle.LiveData

/**
 * @author eneim (2018/05/03).
 */
class AbsentLiveData<T : Any?> private constructor() : LiveData<T>() {
  init {
    // use post instead of set since this can be created on any thread
    postValue(null)
  }

  companion object {
    fun <T> create(): LiveData<T> {
      return AbsentLiveData()
    }
  }
}