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

package app.attiq.common

sealed class Resource<out E : Throwable, out T> {

  /**
   * @param data The result of a success request.
   */
  data class Success<T>(val data: T) : Resource<Nothing, T>()

  /**
   * @param error The error of this failure.
   * @param snapshot The last available data snapshot, or null.
   */
  data class Failure<out E : Throwable, out T>(val error: E, val snapshot: T?) : Resource<E, T>()

  /**
   * @param snapshot The last available data snapshot, or null.
   */
  data class Loading<out T>(val snapshot: T?) : Resource<Nothing, T>()
}
