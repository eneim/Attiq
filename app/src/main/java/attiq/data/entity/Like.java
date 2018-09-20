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
 * Like
 * <p>
 * <strong>The Like API on Qiita:Team has been deprecated since Oct 24 2017. Please use the Emoji reaction API instead.</strong> Represents a like to an item.
 */
public class Like {

  /**
   * Date-time when this data was created
   * (Required)
   */
  @Json(name = "created_at") private Date createdAt;
  /**
   * User
   * <p>
   * A Qiita user (a.k.a. account)
   * (Required)
   */
  @Json(name = "user") private User user;

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
   * User
   * <p>
   * A Qiita user (a.k.a. account)
   * (Required)
   */
  public User getUser() {
    return user;
  }

  /**
   * User
   * <p>
   * A Qiita user (a.k.a. account)
   * (Required)
   */
  public void setUser(User user) {
    this.user = user;
  }
}
