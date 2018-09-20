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

import com.squareup.moshi.Json;
import java.util.Date;

/**
 * Group
 * <p>
 * Represents a group on Qiita:Team
 */
public class Group {

  /**
   * Date-time when this data was created
   * (Required)
   */
  @Json(name = "created_at") private Date createdAt;
  /**
   * A group unique ID
   * (Required)
   */
  @Json(name = "id") private Integer id;
  /**
   * Group name for display.
   * (Required)
   */
  @Json(name = "name") private String name;
  /**
   * A flag to tell which this group is private or public.
   * (Required)
   */
  @Json(name = "private") private Boolean isPrivate;
  /**
   * Date-time when this data was updated
   * (Required)
   */
  @Json(name = "updated_at") private Date updatedAt;
  /**
   * Unique name on a team.
   * (Required)
   */
  @Json(name = "url_name") private String urlName;

  /**
   * Date-time when this data was created
   * (Required)
   */
  public Date getCreatedAt() {
    return createdAt;
  }

  /**
   * Date-time when this data was created
   * (Required)
   */
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * A group unique ID
   * (Required)
   */
  public Integer getId() {
    return id;
  }

  /**
   * A group unique ID
   * (Required)
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Group name for display.
   * (Required)
   */
  public String getName() {
    return name;
  }

  /**
   * Group name for display.
   * (Required)
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * A flag to tell which this group is private or public.
   * (Required)
   */
  public Boolean getPrivate() {
    return isPrivate;
  }

  /**
   * A flag to tell which this group is private or public.
   * (Required)
   */
  public void setPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  /**
   * Date-time when this data was updated
   * (Required)
   */
  public Date getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Date-time when this data was updated
   * (Required)
   */
  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  /**
   * Unique name on a team.
   * (Required)
   */
  public String getUrlName() {
    return urlName;
  }

  /**
   * Unique name on a team.
   * (Required)
   */
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
}
