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

package im.ene.attiq.ui.base

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import attiq.x.base.AttiqPref
import attiq.data.dao.AttiqDb
import attiq.data.dao.ItemDao
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named

/**
 * @author eneim (2018/05/09).
 */
class BaseViewModel @Inject constructor(
    private val pref: AttiqPref,
    private val db: AttiqDb,
    @Named("disk.executor") private val executor: Executor,
    private val itemDao: ItemDao
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    token.value = pref.getToken() // may not change
  }

  private val token = MutableLiveData<String>()
  private val observer: Observer<String> = Observer { reset() }

  init {
    token.value = pref.getToken()
    token.observeForever { observer }
    pref.addListener(this)
  }

  override fun onCleared() {
    token.removeObserver(observer)
    pref.removeListener(this)
    super.onCleared()
  }

  private fun reset() {
    executor.execute { itemDao.deleteAllCurrentItems() }
  }
}