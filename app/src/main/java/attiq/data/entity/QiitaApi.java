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
 * Qiita API v2 JSON Schema
 * <p>
 * In this schema file, we represents the public interface of Qiita API v2 in JSON Hyper Schema draft v4.
 */
public class QiitaApi {

  /**
   * Access token
   * <p>
   * Access token for Qiita API v2
   * (Required)
   */
  @Json(name = "access_token") private AccessToken accessToken;
  /**
   * Authenticated user
   * <p>
   * An user currently authenticated by a given access token. This resources has more detailed information than normal User resource.
   * (Required)
   */
  @Json(name = "authenticated_user") private AuthenticatedUser authenticatedUser;
  /**
   * Comment
   * <p>
   * A comment posted on an item
   * (Required)
   */
  @Json(name = "comment") private Comment comment;
  /**
   * Expanded template
   * <p>
   * You can preview the expanded result of a given template. This is available only on Qiita:Team.
   * (Required)
   */
  @Json(name = "expanded_template") private ExpandedTemplate expandedTemplate;
  /**
   * Group
   * <p>
   * Represents a group on Qiita:Team
   * (Required)
   */
  @Json(name = "group") private Group group;
  /**
   * Item
   * <p>
   * Represents an item posted from a user
   * (Required)
   */
  @Json(name = "item") private Item item;
  /**
   * Like
   * <p>
   * <strong>The Like API on Qiita:Team has been deprecated since Oct 24 2017. Please use the Emoji reaction API instead.</strong> Represents a like to an item.
   * (Required)
   */
  @Json(name = "like") private Like like;
  /**
   * Project
   * <p>
   * Represents a project on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  @Json(name = "project") private Project project;
  /**
   * Emoji reaction
   * <p>
   * An emoji reaction on Qiita:Team (only availabble on Qiita:Team).
   * (Required)
   */
  @Json(name = "reaction") private Reaction reaction;
  /**
   * Tag
   * <p>
   * A tag attached to an item
   * (Required)
   */
  @Json(name = "tag") private TagInfo tag;
  /**
   * Tagging
   * <p>
   * Represents an association between an item and a tag.
   * (Required)
   */
  @Json(name = "tagging") private Tag tagging;
  /**
   * Team
   * <p>
   * Represents a team on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  @Json(name = "team") private Team team;
  /**
   * Invited member
   * <p>
   * Represents members who are invited to on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  @Json(name = "team_invitation") private TeamInvitation teamInvitation;
  /**
   * Template
   * <p>
   * Represents a template for generating an item boilerplate (only available on Qiita:Team).
   * (Required)
   */
  @Json(name = "template") private Template template;
  /**
   * User
   * <p>
   * A Qiita user (a.k.a. account)
   * (Required)
   */
  @Json(name = "user") private User user;

  /**
   * Access token
   * <p>
   * Access token for Qiita API v2
   * (Required)
   */
  public AccessToken getAccessToken() {
    return accessToken;
  }

  /**
   * Access token
   * <p>
   * Access token for Qiita API v2
   * (Required)
   */
  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * Authenticated user
   * <p>
   * An user currently authenticated by a given access token. This resources has more detailed information than normal User resource.
   * (Required)
   */
  public AuthenticatedUser getAuthenticatedUser() {
    return authenticatedUser;
  }

  /**
   * Authenticated user
   * <p>
   * An user currently authenticated by a given access token. This resources has more detailed information than normal User resource.
   * (Required)
   */
  public void setAuthenticatedUser(AuthenticatedUser authenticatedUser) {
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Comment
   * <p>
   * A comment posted on an item
   * (Required)
   */
  public Comment getComment() {
    return comment;
  }

  /**
   * Comment
   * <p>
   * A comment posted on an item
   * (Required)
   */
  public void setComment(Comment comment) {
    this.comment = comment;
  }

  /**
   * Expanded template
   * <p>
   * You can preview the expanded result of a given template. This is available only on Qiita:Team.
   * (Required)
   */
  public ExpandedTemplate getExpandedTemplate() {
    return expandedTemplate;
  }

  /**
   * Expanded template
   * <p>
   * You can preview the expanded result of a given template. This is available only on Qiita:Team.
   * (Required)
   */
  public void setExpandedTemplate(ExpandedTemplate expandedTemplate) {
    this.expandedTemplate = expandedTemplate;
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
   * Item
   * <p>
   * Represents an item posted from a user
   * (Required)
   */
  public Item getItem() {
    return item;
  }

  /**
   * Item
   * <p>
   * Represents an item posted from a user
   * (Required)
   */
  public void setItem(Item item) {
    this.item = item;
  }

  /**
   * Like
   * <p>
   * <strong>The Like API on Qiita:Team has been deprecated since Oct 24 2017. Please use the Emoji reaction API instead.</strong> Represents a like to an item.
   * (Required)
   */
  public Like getLike() {
    return like;
  }

  /**
   * Like
   * <p>
   * <strong>The Like API on Qiita:Team has been deprecated since Oct 24 2017. Please use the Emoji reaction API instead.</strong> Represents a like to an item.
   * (Required)
   */
  public void setLike(Like like) {
    this.like = like;
  }

  /**
   * Project
   * <p>
   * Represents a project on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  public Project getProject() {
    return project;
  }

  /**
   * Project
   * <p>
   * Represents a project on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  public void setProject(Project project) {
    this.project = project;
  }

  /**
   * Emoji reaction
   * <p>
   * An emoji reaction on Qiita:Team (only availabble on Qiita:Team).
   * (Required)
   */
  public Reaction getReaction() {
    return reaction;
  }

  /**
   * Emoji reaction
   * <p>
   * An emoji reaction on Qiita:Team (only availabble on Qiita:Team).
   * (Required)
   */
  public void setReaction(Reaction reaction) {
    this.reaction = reaction;
  }

  /**
   * Tag
   * <p>
   * A tag attached to an item
   * (Required)
   */
  public TagInfo getTag() {
    return tag;
  }

  /**
   * Tag
   * <p>
   * A tag attached to an item
   * (Required)
   */
  public void setTag(TagInfo tag) {
    this.tag = tag;
  }

  /**
   * Tagging
   * <p>
   * Represents an association between an item and a tag.
   * (Required)
   */
  public Tag getTagging() {
    return tagging;
  }

  /**
   * Tagging
   * <p>
   * Represents an association between an item and a tag.
   * (Required)
   */
  public void setTagging(Tag tagging) {
    this.tagging = tagging;
  }

  /**
   * Team
   * <p>
   * Represents a team on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  public Team getTeam() {
    return team;
  }

  /**
   * Team
   * <p>
   * Represents a team on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  public void setTeam(Team team) {
    this.team = team;
  }

  /**
   * Invited member
   * <p>
   * Represents members who are invited to on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  public TeamInvitation getTeamInvitation() {
    return teamInvitation;
  }

  /**
   * Invited member
   * <p>
   * Represents members who are invited to on Qiita:Team (only available on Qiita:Team).
   * (Required)
   */
  public void setTeamInvitation(TeamInvitation teamInvitation) {
    this.teamInvitation = teamInvitation;
  }

  /**
   * Template
   * <p>
   * Represents a template for generating an item boilerplate (only available on Qiita:Team).
   * (Required)
   */
  public Template getTemplate() {
    return template;
  }

  /**
   * Template
   * <p>
   * Represents a template for generating an item boilerplate (only available on Qiita:Team).
   * (Required)
   */
  public void setTemplate(Template template) {
    this.template = template;
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
