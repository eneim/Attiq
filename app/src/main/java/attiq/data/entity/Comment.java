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
 * Comment
 * <p>
 * A comment posted on an item
 */
public class Comment {

  /**
   * Comment body in Markdown
   * (Required)
   */
  @Json(name = "body") private String body;
  /**
   * Date-time when this data was created
   * (Required)
   */
  @Json(name = "created_at") private Date createdAt;
  /**
   * Comment unique ID
   * (Required)
   */
  @Json(name = "id") private String id;
  /**
   * Comment body in HTML
   * (Required)
   */
  @Json(name = "rendered_body") private String renderedBody;
  /**
   * Date-time when this data was updated
   * (Required)
   */
  @Json(name = "updated_at") private Date updatedAt;
  /**
   * User
   * <p>
   * A Qiita user (a.k.a. account)
   * (Required)
   */
  @Json(name = "user") private User user;

  /**
   * Comment body in Markdown
   * (Required)
   */
  public String getBody() {
    return body;
  }

  /**
   * Comment body in Markdown
   * (Required)
   */
  public void setBody(String body) {
    this.body = body;
  }

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
   * Comment unique ID
   * (Required)
   */
  public String getId() {
    return id;
  }

  /**
   * Comment unique ID
   * (Required)
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Comment body in HTML
   * (Required)
   */
  public String getRenderedBody() {
    return renderedBody;
  }

  /**
   * Comment body in HTML
   * (Required)
   */
  public void setRenderedBody(String renderedBody) {
    this.renderedBody = renderedBody;
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
