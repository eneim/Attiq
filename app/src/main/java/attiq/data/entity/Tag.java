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
import androidx.room.PrimaryKey;
import com.squareup.moshi.Json;
import java.util.List;

@Entity //
public class Tag {

  public Tag() {
  }

  /**
   * (Required)
   */
  @PrimaryKey @ColumnInfo(name = "tag_name") @NonNull //
  @Json(name = "name") private String name;
  @Json(name = "versions") private List<String> versions = null;

  /**
   * (Required)
   */
  @NonNull public String getName() {
    return name;
  }

  /**
   * (Required)
   */
  public void setName(@NonNull String name) {
    this.name = name;
  }

  public List<String> getVersions() {
    return versions;
  }

  public void setVersions(List<String> versions) {
    this.versions = versions;
  }
}
