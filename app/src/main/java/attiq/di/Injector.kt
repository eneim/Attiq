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

package attiq.di

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import im.ene.attiq.Attiq
import im.ene.attiq.BuildConfig

/**
 * @author eneim (2018/03/18).
 */
interface Injectable

object Injector {

  fun init(attiq: Attiq) {
    val appComponent = DaggerAppComponent.builder()
        .app(attiq)
        .pref(PreferenceManager.getDefaultSharedPreferences(attiq))
        .apiBaseUrl(BuildConfig.BASE_URL)
        .networkCacheDir(attiq.cacheDir)
        .build()

    appComponent.inject(attiq)

    attiq.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
      override fun onActivityPaused(activity: Activity?) {}

      override fun onActivityResumed(activity: Activity?) {}

      override fun onActivityStarted(activity: Activity?) {}

      override fun onActivityDestroyed(activity: Activity?) {}

      override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

      override fun onActivityStopped(activity: Activity?) {}

      override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        handleActivityOnCreated(activity)
      }
    })
  }

  fun handleActivityOnCreated(activity: Activity?) {
    if (activity is HasSupportFragmentInjector || activity is Injectable) {
      AndroidInjection.inject(activity)
    }

    if (activity is FragmentActivity) {
      activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
          object : FragmentLifecycleCallbacks() {
            override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment,
                savedInstanceState: Bundle?) {
              super.onFragmentPreCreated(fm, f, savedInstanceState)
              if (f is Injectable) AndroidSupportInjection.inject(f)
            }
          }, false)
    }
  }
}