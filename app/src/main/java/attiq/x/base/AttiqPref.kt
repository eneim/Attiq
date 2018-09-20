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

package attiq.x.base

import android.content.SharedPreferences

/**
 * @author eneim (2018/03/30).
 */
interface AttiqPref {

  /**
   * Return a valid token, or empty String.
   */
  fun getToken(): String

  fun setToken(token: String?)

  fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)

  fun removeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}