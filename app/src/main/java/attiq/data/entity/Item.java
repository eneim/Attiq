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
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.squareup.moshi.Json;
import java.util.Date;
import java.util.List;

/**
 * Item
 * <p>
 * Represents an item posted from a user
 */
@Entity //
public class Item {

  public Item() {
  }

  /**
   * Item body in HTML
   * (Required)
   */
  @Json(name = "rendered_body") private String renderedBody;  //

  /**
   * Item body in Markdown
   * (Required)
   */
  @Json(name = "body") private String body;  //

  /**
   * A flag whether this item is co-edit mode (only available on Qiita:Team)
   * (Required)
   */
  @Json(name = "coediting") //
  private Boolean coediting;

  /**
   * Comments count
   * (Required)
   */
  @Json(name = "comments_count")  //
  private Integer commentsCount;

  /**
   * Date-time when this data was created
   * (Required)
   */
  @Json(name = "created_at")  //
  private Date createdAt;

  /**
   * Group
   * <p>
   * Represents a group on Qiita:Team
   * (Required)
   */
  @Ignore @Json(name = "group") //
  private Group group;

  /**
   * An unique item ID
   * (Required)
   */
  @PrimaryKey @ColumnInfo(name = "item_id") @NonNull @Json(name = "id")  //
  private String itemId;

  /**
   * Likes count (only available on Qiita)
   * (Required)
   */
  @Json(name = "likes_count") //
  private Integer likesCount;

  /**
   * A flag whether this item is private (only available on Qiita)
   * (Required)
   */
  @Json(name = "private") //
  private Boolean isPrivate;

  /**
   * Emoji reactions count (only available on Qiita:Team)
   * (Required)
   */
  @Json(name = "reactions_count") //
  private Integer reactionsCount;

  /**
   * A list of tags
   * (Required)
   */
  @Ignore
  // @Embedded @Json(name = "tags")  //
  private List<Tag> tags = null;

  /**
   * The title of this item
   * (Required)
   */
  @Json(name = "title") //
  private String title;

  /**
   * Date-time when this data was updated
   * (Required)
   */
  @Json(name = "updated_at")  //
  private Date updatedAt;

  /**
   * The URL of this item
   * (Required)
   */
  @Json(name = "url") //
  private String url;

  /**
   * User
   * <p>
   * A Qiita user (a.k.a. account)
   * (Required)
   */
  @Embedded @Json(name = "user")  //
  private User user;

  /**
   * The number of views.
   * (Required)
   */
  @Json(name = "page_views_count")  //
  private Integer pageViewsCount;

  /**
   * Item body in HTML
   * (Required)
   */
  public String getRenderedBody() {
    return renderedBody;
  }

  /**
   * Item body in HTML
   * (Required)
   */
  public void setRenderedBody(String renderedBody) {
    this.renderedBody = renderedBody;
  }

  /**
   * Item body in Markdown
   * (Required)
   */
  public String getBody() {
    return body;
  }

  /**
   * Item body in Markdown
   * (Required)
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * A flag whether this item is co-edit mode (only available on Qiita:Team)
   * (Required)
   */
  public Boolean getCoediting() {
    return coediting;
  }

  /**
   * A flag whether this item is co-edit mode (only available on Qiita:Team)
   * (Required)
   */
  public void setCoediting(Boolean coediting) {
    this.coediting = coediting;
  }

  /**
   * Comments count
   * (Required)
   */
  public Integer getCommentsCount() {
    return commentsCount;
  }

  /**
   * Comments count
   * (Required)
   */
  public void setCommentsCount(Integer commentsCount) {
    this.commentsCount = commentsCount;
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
   * Group
   * <p>
   * Represents a group on Qiita:Team
   * (Required)
   */
  public Group getGroup() {
    return group;
  }

  /**
   * Group
   * <p>
   * Represents a group on Qiita:Team
   * (Required)
   */
  public void setGroup(Group group) {
    this.group = group;
  }

  /**
   * An unique item ID
   * (Required)
   */
  @NonNull public String getItemId() {
    return itemId;
  }

  /**
   * An unique item ID
   * (Required)
   */
  public void setItemId(@NonNull String itemId) {
    this.itemId = itemId;
  }

  /**
   * Likes count (only available on Qiita)
   * (Required)
   */
  public Integer getLikesCount() {
    return likesCount;
  }

  /**
   * Likes count (only available on Qiita)
   * (Required)
   */
  public void setLikesCount(Integer likesCount) {
    this.likesCount = likesCount;
  }

  /**
   * A flag whether this item is private (only available on Qiita)
   * (Required)
   */
  public Boolean getPrivate() {
    return isPrivate;
  }

  /**
   * A flag whether this item is private (only available on Qiita)
   * (Required)
   */
  public void setPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  /**
   * Emoji reactions count (only available on Qiita:Team)
   * (Required)
   */
  public Integer getReactionsCount() {
    return reactionsCount;
  }

  /**
   * Emoji reactions count (only available on Qiita:Team)
   * (Required)
   */
  public void setReactionsCount(Integer reactionsCount) {
    this.reactionsCount = reactionsCount;
  }

  /**
   * A list of tags
   * (Required)
   */
  public List<Tag> getTags() {
    return tags;
  }

  /**
   * A list of tags
   * (Required)
   */
  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  /**
   * The title of this item
   * (Required)
   */
  public String getTitle() {
    return title;
  }

  /**
   * The title of this item
   * (Required)
   */
  public void setTitle(String title) {
    this.title = title;
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
   * The URL of this item
   * (Required)
   */
  public String getUrl() {
    return url;
  }

  /**
   * The URL of this item
   * (Required)
   */
  public void setUrl(String url) {
    this.url = url;
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

  /**
   * The number of views.
   * (Required)
   */
  public Integer getPageViewsCount() {
    return pageViewsCount;
  }

  /**
   * The number of views.
   * (Required)
   */
  public void setPageViewsCount(Integer pageViewsCount) {
    this.pageViewsCount = pageViewsCount;
  }
}
