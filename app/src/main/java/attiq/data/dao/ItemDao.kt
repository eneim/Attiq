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
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import attiq.data.entity.Item
import attiq.data.entity.ItemTagJoin
import attiq.data.entity.Tag

/**
 * @author eneim (2018/05/04).
 */
@Dao
abstract class ItemDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insert(vararg items: Item)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertItem(item: Item): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertItems(items: List<Item>)

  @Query("SELECT * FROM item WHERE item_id = :id")
  abstract fun load(id: String): LiveData<Item>

  @Query("DELETE FROM item")
  abstract fun deleteAllCurrentItems()

  @Insert
  abstract fun insertItemTagJoins(items: List<ItemTagJoin>)

  @Delete
  abstract fun deleteItemTagJoins(vararg item: ItemTagJoin)

  @Transaction
  @Query(
      "SELECT tag.* FROM tag INNER JOIN item_tag_join ON tag.tag_name = item_tag_join.tag_name WHERE item_tag_join.item_id = :itemId")
  abstract fun getTagsForItem(itemId: String): LiveData<List<Tag>>

  @Query("SELECT * FROM item ORDER BY createdAt DESC")
  abstract fun loadItemsDataSource(): DataSource.Factory<Int, Item>

  @Query("SELECT * FROM item ORDER BY createdAt DESC")
  abstract fun loadItems(): List<Item>
}