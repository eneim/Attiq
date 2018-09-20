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
 * Expanded template
 * <p>
 * You can preview the expanded result of a given template. This is available only on Qiita:Team.
 */
public class ExpandedTemplate {

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
}
