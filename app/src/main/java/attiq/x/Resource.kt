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

import attiq.x.Resource.Status.ERROR
import attiq.x.Resource.Status.LOADING
import attiq.x.Resource.Status.SUCCESS

/**
 * @author eneim (2018/04/22).
 *
 * Ref: https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/vo/Resource.java
 */
data class Resource<T>(val status: Status, val message: String?, val data: T?) {
  sealed class Status {
    object LOADING : Status()
    object SUCCESS : Status()
    object ERROR : Status()
  }

  companion object {
    fun <T> success(value: T?) = Resource(SUCCESS, null, value)
    fun <T> loading(value: T?) = Resource(LOADING, null, value)
    fun <T> error(msg: String, value: T?) = Resource(ERROR, msg, value)
  }
}