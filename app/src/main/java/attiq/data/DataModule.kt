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

import android.app.Application
import androidx.room.Room
import attiq.data.dao.AttiqDb
import dagger.Module
import dagger.Provides
import im.ene.attiq.BuildConfig
import javax.inject.Singleton

/**
 * @author eneim (2018/03/18).
 */
// Method name looks sick, I know. So just leave it in the DI and forget the detail.
@Module
class DataModule {

  @Singleton
  @Provides
  fun provideDb(app: Application): AttiqDb {
    return Room.databaseBuilder(app, AttiqDb::class.java, BuildConfig.DB_NAME)
        .fallbackToDestructiveMigration()
        .build()
  }

  @Singleton
  @Provides
  fun provideUserDao(db: AttiqDb) = db.userDao()

  @Singleton
  @Provides
  fun provideItemDao(db: AttiqDb) = db.itemDao()

  @Singleton
  @Provides
  fun provideTagDao(db: AttiqDb) = db.tagDao()
}