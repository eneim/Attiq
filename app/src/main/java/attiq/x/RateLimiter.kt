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

import android.os.SystemClock
import androidx.collection.ArrayMap
import java.util.concurrent.TimeUnit

/**
 * @author eneim (2018/04/23).
 *
 * Ref: https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/util/RateLimiter.java
 */
class RateLimiter<KEY>(timeout: Int, timeUnit: TimeUnit) {
  private val timestamps = ArrayMap<KEY, Long>()
  private val timeout: Long = timeUnit.toMillis(timeout.toLong())

  @Synchronized
  fun shouldFetch(key: KEY): Boolean {
    val lastFetched = timestamps[key]
    val now = now()
    if (lastFetched == null) {
      timestamps[key] = now
      return true
    }
    if (now - lastFetched > timeout) {
      timestamps[key] = now
      return true
    }
    return false
  }

  private fun now(): Long {
    return SystemClock.uptimeMillis()
  }

  @Synchronized
  fun reset(key: KEY) {
    timestamps.remove(key)
  }
}
