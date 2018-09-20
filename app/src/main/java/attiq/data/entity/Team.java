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
import com.squareup.moshi.Json;

/**
 * Team
 * <p>
 * Represents a team on Qiita:Team (only available on Qiita:Team).
 */
public class Team {

  /**
   * A flag whether this team is active or not
   * (Required)
   */
  @Json(name = "active") private Boolean active;
  /**
   * A unique team ID
   * (Required)
   */
  @NonNull @Json(name = "id") private String id;
  /**
   * The team name configured for this team
   * (Required)
   */
  @Json(name = "name") private String name;

  /**
   * A flag whether this team is active or not
   * (Required)
   */
  public Boolean getActive() {
    return active;
  }

  /**
   * A flag whether this team is active or not
   * (Required)
   */
  public void setActive(Boolean active) {
    this.active = active;
  }

  /**
   * A unique team ID
   * (Required)
   */
  public String getId() {
    return id;
  }

  /**
   * A unique team ID
   * (Required)
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * The team name configured for this team
   * (Required)
   */
  public String getName() {
    return name;
  }

  /**
   * The team name configured for this team
   * (Required)
   */
  public void setName(String name) {
    this.name = name;
  }
}
