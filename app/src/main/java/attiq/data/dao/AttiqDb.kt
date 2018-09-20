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

package attiq.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import attiq.data.AttiqTypeConverters
import attiq.data.entity.Item
import attiq.data.entity.ItemTagJoin
import attiq.data.entity.Tag
import attiq.data.entity.User

/**
 * @author eneim (2018/05/04).
 */
@Database(
    entities = [
      User::class,
      Item::class,
      Tag::class,
      ItemTagJoin::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AttiqTypeConverters::class)
abstract class AttiqDb : RoomDatabase() {

  abstract fun itemDao(): ItemDao

  abstract fun userDao(): UserDao

  abstract fun tagDao(): TagDao
}