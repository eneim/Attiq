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
 * Project
 * <p>
 * Represents a project on Qiita:Team (only available on Qiita:Team).
 */
public class Project {

  /**
   * The projects page body in HTML
   * (Required)
   */
  @Json(name = "rendered_body") private String renderedBody;
  /**
   * A flag whether this project is archived
   * (Required)
   */
  @Json(name = "archived") private Boolean archived;
  /**
   * The project page body in Markdown
   * (Required)
   */
  @Json(name = "body") private String body;
  /**
   * Date-time when this data was created
   * (Required)
   */
  @Json(name = "created_at") private Date createdAt;
  /**
   * A project ID unique in its team
   * (Required)
   */
  @Json(name = "id") private Integer id;
  /**
   * The name of a project
   * (Required)
   */
  @Json(name = "name") private String name;
  /**
   * Emoji reactions count
   * (Required)
   */
  @Json(name = "reactions_count") private Integer reactionsCount;
  /**
   * Date-time when this data was updated
   * (Required)
   */
  @Json(name = "updated_at") private Date updatedAt;

  /**
   * The projects page body in HTML
   * (Required)
   */
  public String getRenderedBody() {
    return renderedBody;
  }

  /**
   * The projects page body in HTML
   * (Required)
   */
  public void setRenderedBody(String renderedBody) {
    this.renderedBody = renderedBody;
  }

  /**
   * A flag whether this project is archived
   * (Required)
   */
  public Boolean getArchived() {
    return archived;
  }

  /**
   * A flag whether this project is archived
   * (Required)
   */
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  /**
   * The project page body in Markdown
   * (Required)
   */
  public String getBody() {
    return body;
  }

  /**
   * The project page body in Markdown
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
   * A project ID unique in its team
   * (Required)
   */
  public Integer getId() {
    return id;
  }

  /**
   * A project ID unique in its team
   * (Required)
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * The name of a project
   * (Required)
   */
  public String getName() {
    return name;
  }

  /**
   * The name of a project
   * (Required)
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Emoji reactions count
   * (Required)
   */
  public Integer getReactionsCount() {
    return reactionsCount;
  }

  /**
   * Emoji reactions count
   * (Required)
   */
  public void setReactionsCount(Integer reactionsCount) {
    this.reactionsCount = reactionsCount;
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
}
