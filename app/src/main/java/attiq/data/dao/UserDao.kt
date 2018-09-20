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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import attiq.data.entity.User

/**
 * @author eneim (2018/05/04).
 */
@Dao
abstract class UserDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insert(vararg users: User)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertUser(user: User)

  @Query("SELECT * FROM user WHERE user_id = :id")
  abstract fun load(id: String): LiveData<User>
}