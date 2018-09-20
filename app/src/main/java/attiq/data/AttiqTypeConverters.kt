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

import androidx.room.TypeConverter
import attiq.data.entity.Tag
import java.util.Date


/**
 * @author eneim (2018/05/04).
 */
class AttiqTypeConverters {

  @TypeConverter
  fun fromDate(date: Date?): Long? {
    return date?.time
  }

  @TypeConverter
  fun toDate(millisSinceEpoch: Long?): Date? {
    return if (millisSinceEpoch == null) {
      null
    } else Date(millisSinceEpoch)
  }

  @TypeConverter
  fun fromStringsToString(strings: List<String>): String {
    return if (strings.isEmpty()) ""
    else strings.joinToString(separator = "|")
  }

  @TypeConverter
  fun fromStringToStrings(string: String?): List<String> {
    return string?.split("|")?.toList() ?: emptyList()
  }

  fun fromTagListToString(tags: List<Tag>) {
    tags.toTypedArray().toString()
  }
}