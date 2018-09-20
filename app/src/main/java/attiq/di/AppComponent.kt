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

import android.app.Application
import android.content.SharedPreferences
import attiq.data.ApiModule
import attiq.data.DataModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import im.ene.attiq.Attiq
import java.io.File
import javax.inject.Singleton

/**
 * @author eneim (2018/03/18).
 */
@Singleton
@Component(
    modules = [
      AndroidInjectionModule::class,
      AndroidSupportInjectionModule::class,
      ActivityModule::class,
      FragmentModule::class,
      ApiModule::class,
      DataModule::class
    ]
)
interface AppComponent {

  @dagger.Component.Builder
  interface Builder {
    @BindsInstance
    fun app(app: Application): Builder

    @BindsInstance
    fun pref(pref: SharedPreferences): Builder

    @BindsInstance
    fun apiBaseUrl(baseUrl: String): Builder

    @BindsInstance
    fun networkCacheDir(cache: File): Builder

    // Build
    fun build(): AppComponent
  }

  fun inject(attiq: Attiq)
}