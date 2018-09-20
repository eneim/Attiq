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
import java.util.Set;

/**
 * Template
 * <p>
 * Represents a template for generating an item boilerplate (only available on Qiita:Team).
 */
public class Template {

  /**
   * The body of this template
   * (Required)
   */
  @Json(name = "body") private String body;
  /**
   * A unique template ID
   * (Required)
   */
  @Json(name = "id") private Integer id;
  /**
   * A template name
   * (Required)
   */
  @Json(name = "name") private String name;
  /**
   * An item body where variables are expanded
   * (Required)
   */
  @Json(name = "expanded_body") private String expandedBody;
  /**
   * A list of tags where variables are expanded
   * (Required)
   */
  @Json(name = "expanded_tags") private Set<Tag> expandedTags = null;
  /**
   * An item title where variables are expanded
   * (Required)
   */
  @Json(name = "expanded_title") private String expandedTitle;
  /**
   * A list of tags
   * (Required)
   */
  @Json(name = "tags") private Set<Tag> tags = null;
  /**
   * A template title where variables are to be expanded
   * (Required)
   */
  @Json(name = "title") private String title;

  /**
   * The body of this template
   * (Required)
   */
  public String getBody() {
    return body;
  }

  /**
   * The body of this template
   * (Required)
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * A unique template ID
   * (Required)
   */
  public Integer getId() {
    return id;
  }

  /**
   * A unique template ID
   * (Required)
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * A template name
   * (Required)
   */
  public String getName() {
    return name;
  }

  /**
   * A template name
   * (Required)
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * An item body where variables are expanded
   * (Required)
   */
  public String getExpandedBody() {
    return expandedBody;
  }

  /**
   * An item body where variables are expanded
   * (Required)
   */
  public void setExpandedBody(String expandedBody) {
    this.expandedBody = expandedBody;
  }

  /**
   * A list of tags where variables are expanded
   * (Required)
   */
  public Set<Tag> getExpandedTags() {
    return expandedTags;
  }

  /**
   * A list of tags where variables are expanded
   * (Required)
   */
  public void setExpandedTags(Set<Tag> expandedTags) {
    this.expandedTags = expandedTags;
  }

  /**
   * An item title where variables are expanded
   * (Required)
   */
  public String getExpandedTitle() {
    return expandedTitle;
  }

  /**
   * An item title where variables are expanded
   * (Required)
   */
  public void setExpandedTitle(String expandedTitle) {
    this.expandedTitle = expandedTitle;
  }

  /**
   * A list of tags
   * (Required)
   */
  public Set<Tag> getTags() {
    return tags;
  }

  /**
   * A list of tags
   * (Required)
   */
  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  /**
   * A template title where variables are to be expanded
   * (Required)
   */
  public String getTitle() {
    return title;
  }

  /**
   * A template title where variables are to be expanded
   * (Required)
   */
  public void setTitle(String title) {
    this.title = title;
  }
}
