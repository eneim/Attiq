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
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

/**
 * @author eneim (2018/03/30).
 */
class AttiqPrefImpl(private val pref: SharedPreferences) : AttiqPref {
  override fun addListener(listener: OnSharedPreferenceChangeListener) {
    pref.registerOnSharedPreferenceChangeListener(listener)
  }

  override fun removeListener(listener: OnSharedPreferenceChangeListener) {
    pref.unregisterOnSharedPreferenceChangeListener(listener)
  }

  companion object {
    private const val EMPTY = ""
    private const val KEY_TOKEN = "attiq:pref:token"
  }

  override fun getToken() = pref.getString(KEY_TOKEN,
      EMPTY) as String

  override fun setToken(token: String?) {
    pref.edit().putString(
        KEY_TOKEN, token ?: EMPTY).apply()
  }
}