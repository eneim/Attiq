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
 * Authenticated user
 * <p>
 * An user currently authenticated by a given access token. This resources has more detailed information than normal User resource.
 */
public class AuthenticatedUser {

  /**
   * self-description
   * (Required)
   */
  @Json(name = "description") private String description;
  /**
   * Facebook ID
   * (Required)
   */
  @Json(name = "facebook_id") private String facebookId;
  /**
   * Followees count
   * (Required)
   */
  @Json(name = "followees_count") private Integer followeesCount;
  /**
   * Followers count
   * (Required)
   */
  @Json(name = "followers_count") private Integer followersCount;
  /**
   * GitHub ID
   * (Required)
   */
  @Json(name = "github_login_name") private String githubLoginName;
  /**
   * User ID
   * (Required)
   */
  @Json(name = "id") private String id;
  /**
   * How many items a user posted on qiita.com (Items on Qiita:Team are not included)
   * (Required)
   */
  @Json(name = "items_count") private Integer itemsCount;
  /**
   * LinkedIn ID
   * (Required)
   */
  @Json(name = "linkedin_id") private String linkedinId;
  /**
   * Location
   * (Required)
   */
  @Json(name = "location") private String location;
  /**
   * Customized user name
   * (Required)
   */
  @Json(name = "name") private String name;
  /**
   * Organization which a user belongs to
   * (Required)
   */
  @Json(name = "organization") private String organization;
  /**
   * Unique integer ID
   * (Required)
   */
  @Json(name = "permanent_id") private Integer permanentId;
  /**
   * Profile image URL
   * (Required)
   */
  @Json(name = "profile_image_url") private String profileImageUrl;
  /**
   * Twitter screen name
   * (Required)
   */
  @Json(name = "twitter_screen_name") private String twitterScreenName;
  /**
   * Website URL
   * (Required)
   */
  @Json(name = "website_url") private String websiteUrl;
  /**
   * Monthly image upload limit
   * (Required)
   */
  @Json(name = "image_monthly_upload_limit") private Integer imageMonthlyUploadLimit;
  /**
   * Monthly remaining image upload size
   * (Required)
   */
  @Json(name = "image_monthly_upload_remaining") private Integer imageMonthlyUploadRemaining;
  /**
   * A flag whether this user is configured as team-only
   * (Required)
   */
  @Json(name = "team_only") private Boolean teamOnly;

  /**
   * self-description
   * (Required)
   */
  public String getDescription() {
    return description;
  }

  /**
   * self-description
   * (Required)
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Facebook ID
   * (Required)
   */
  public String getFacebookId() {
    return facebookId;
  }

  /**
   * Facebook ID
   * (Required)
   */
  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }

  /**
   * Followees count
   * (Required)
   */
  public Integer getFolloweesCount() {
    return followeesCount;
  }

  /**
   * Followees count
   * (Required)
   */
  public void setFolloweesCount(Integer followeesCount) {
    this.followeesCount = followeesCount;
  }

  /**
   * Followers count
   * (Required)
   */
  public Integer getFollowersCount() {
    return followersCount;
  }

  /**
   * Followers count
   * (Required)
   */
  public void setFollowersCount(Integer followersCount) {
    this.followersCount = followersCount;
  }

  /**
   * GitHub ID
   * (Required)
   */
  public String getGithubLoginName() {
    return githubLoginName;
  }

  /**
   * GitHub ID
   * (Required)
   */
  public void setGithubLoginName(String githubLoginName) {
    this.githubLoginName = githubLoginName;
  }

  /**
   * User ID
   * (Required)
   */
  public String getId() {
    return id;
  }

  /**
   * User ID
   * (Required)
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * How many items a user posted on qiita.com (Items on Qiita:Team are not included)
   * (Required)
   */
  public Integer getItemsCount() {
    return itemsCount;
  }

  /**
   * How many items a user posted on qiita.com (Items on Qiita:Team are not included)
   * (Required)
   */
  public void setItemsCount(Integer itemsCount) {
    this.itemsCount = itemsCount;
  }

  /**
   * LinkedIn ID
   * (Required)
   */
  public String getLinkedinId() {
    return linkedinId;
  }

  /**
   * LinkedIn ID
   * (Required)
   */
  public void setLinkedinId(String linkedinId) {
    this.linkedinId = linkedinId;
  }

  /**
   * Location
   * (Required)
   */
  public String getLocation() {
    return location;
  }

  /**
   * Location
   * (Required)
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Customized user name
   * (Required)
   */
  public String getName() {
    return name;
  }

  /**
   * Customized user name
   * (Required)
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Organization which a user belongs to
   * (Required)
   */
  public String getOrganization() {
    return organization;
  }

  /**
   * Organization which a user belongs to
   * (Required)
   */
  public void setOrganization(String organization) {
    this.organization = organization;
  }

  /**
   * Unique integer ID
   * (Required)
   */
  public Integer getPermanentId() {
    return permanentId;
  }

  /**
   * Unique integer ID
   * (Required)
   */
  public void setPermanentId(Integer permanentId) {
    this.permanentId = permanentId;
  }

  /**
   * Profile image URL
   * (Required)
   */
  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  /**
   * Profile image URL
   * (Required)
   */
  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  /**
   * Twitter screen name
   * (Required)
   */
  public String getTwitterScreenName() {
    return twitterScreenName;
  }

  /**
   * Twitter screen name
   * (Required)
   */
  public void setTwitterScreenName(String twitterScreenName) {
    this.twitterScreenName = twitterScreenName;
  }

  /**
   * Website URL
   * (Required)
   */
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  /**
   * Website URL
   * (Required)
   */
  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  /**
   * Monthly image upload limit
   * (Required)
   */
  public Integer getImageMonthlyUploadLimit() {
    return imageMonthlyUploadLimit;
  }

  /**
   * Monthly image upload limit
   * (Required)
   */
  public void setImageMonthlyUploadLimit(Integer imageMonthlyUploadLimit) {
    this.imageMonthlyUploadLimit = imageMonthlyUploadLimit;
  }

  /**
   * Monthly remaining image upload size
   * (Required)
   */
  public Integer getImageMonthlyUploadRemaining() {
    return imageMonthlyUploadRemaining;
  }

  /**
   * Monthly remaining image upload size
   * (Required)
   */
  public void setImageMonthlyUploadRemaining(Integer imageMonthlyUploadRemaining) {
    this.imageMonthlyUploadRemaining = imageMonthlyUploadRemaining;
  }

  /**
   * A flag whether this user is configured as team-only
   * (Required)
   */
  public Boolean getTeamOnly() {
    return teamOnly;
  }

  /**
   * A flag whether this user is configured as team-only
   * (Required)
   */
  public void setTeamOnly(Boolean teamOnly) {
    this.teamOnly = teamOnly;
  }
}
