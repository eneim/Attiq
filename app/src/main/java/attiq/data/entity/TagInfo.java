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

/**
 * Tag
 * <p>
 * A tag attached to an item
 */
public class TagInfo {

  /**
   * Followes count
   * (Required)
   */
  @Json(name = "followers_count") private Integer followersCount;
  /**
   * Tag Icon URL
   * (Required)
   */
  @Json(name = "icon_url") private String iconUrl;
  /**
   * Tag name
   * (Required)
   */
  @Json(name = "id") private String id;
  /**
   * Items count
   * (Required)
   */
  @Json(name = "items_count") private Integer itemsCount;

  /**
   * Followes count
   * (Required)
   */
  public Integer getFollowersCount() {
    return followersCount;
  }

  /**
   * Followes count
   * (Required)
   */
  public void setFollowersCount(Integer followersCount) {
    this.followersCount = followersCount;
  }

  /**
   * Tag Icon URL
   * (Required)
   */
  public String getIconUrl() {
    return iconUrl;
  }

  /**
   * Tag Icon URL
   * (Required)
   */
  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  /**
   * Tag name
   * (Required)
   */
  public String getId() {
    return id;
  }

  /**
   * Tag name
   * (Required)
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Items count
   * (Required)
   */
  public Integer getItemsCount() {
    return itemsCount;
  }

  /**
   * Items count
   * (Required)
   */
  public void setItemsCount(Integer itemsCount) {
    this.itemsCount = itemsCount;
  }
}
