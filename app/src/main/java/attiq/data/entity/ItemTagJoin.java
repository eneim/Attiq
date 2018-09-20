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

package attiq.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

/**
 * @author eneim (2018/05/06).
 */
@Entity(  //
    tableName = "item_tag_join",  //
    primaryKeys = { "item_id", "tag_name" },  //
    foreignKeys = { //
        @ForeignKey(entity = Item.class, parentColumns = "item_id", childColumns = "item_id", onDelete = CASCADE),
        @ForeignKey(entity = Tag.class, parentColumns = "tag_name", childColumns = "tag_name", onDelete = CASCADE)
    },  //
    indices = { //
        @Index(value = "item_id"),  //
        @Index(value = "tag_name")  //
    }//
) //
public class ItemTagJoin {

  @ColumnInfo(name = "item_id") //
  @NonNull public final String itemId;

  @ColumnInfo(name = "tag_name")  //
  @NonNull public final String tagName;

  public ItemTagJoin(@NonNull String itemId, @NonNull String tagName) {
    this.itemId = itemId;
    this.tagName = tagName;
  }
}
