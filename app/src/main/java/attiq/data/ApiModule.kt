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

package attiq.data

import android.content.SharedPreferences
import attiq.x.base.AttiqPref
import attiq.x.base.AttiqPrefImpl
import attiq.x.base.Skedulers
import attiq.x.cookie.WebViewCookieHandler
import attiq.x.cookie.cache.SetCookieCache
import attiq.x.cookie.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.createWithScheduler
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author eneim (2018/03/18).
 */
@Module
class ApiModule {

  @Singleton
  @Provides
  fun provideMoshi(): Moshi = Moshi.Builder()
      // .add(KotlinJsonAdapterFactory()) // this thing adds few thousands methods, quite bad.
      .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
      .build()

  @Singleton
  @Provides
  fun provideAttiqPref(pref: SharedPreferences) = AttiqPrefImpl(
      pref) as AttiqPref

  @Singleton
  @Provides
  @Named("shared.http.client")
  fun provideGeneralOkHttpClient(cache: File): OkHttpClient {
    return OkHttpClient.Builder().cache(Cache(cache, 16 * 1024 * 1024))
        .addInterceptor(HttpLoggingInterceptor()).build()
  }

  @Singleton
  @Provides
  @Named("api.http.client")
  fun provideOkHttpClient(
      @Named("shared.http.client") client: OkHttpClient,
      pref: SharedPreferences,
      attiqPref: AttiqPref): OkHttpClient {
    return client.newBuilder()
        .cookieJar(WebViewCookieHandler(SetCookieCache(),
            SharedPrefsCookiePersistor(pref)))
        .addInterceptor { chain ->
          val token = attiqPref.getToken()
          val requestBuilder = chain.request().newBuilder()
          if (token.isNotEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
          }
          // return result
          chain.proceed(requestBuilder.build())
        }
        .build()
  }

  @Singleton
  @Provides
  fun provideRetrofit(
      baseUrl: String,
      @Named("api.http.client") client: OkHttpClient,
      moshi: Moshi): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addCallAdapterFactory(createWithScheduler(Skedulers.network))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
  }

  @Singleton
  @Provides
  fun provideApiV2(retrofit: Retrofit): ApiV2 = retrofit.create(ApiV2::class.java)

  @Singleton
  @Provides
  @Named("io.executor")
  fun provideIoExecutor() = Executors.newFixedThreadPool(5) as Executor

  @Singleton
  @Provides
  @Named("disk.executor")
  fun provideDiskExecutor() = Executors.newSingleThreadExecutor() as Executor
}