package im.ene.lab.attiq.data.model.two;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by eneim on 12/13/15.
 */
public class User extends RealmObject {

  @SerializedName("description")
  @Expose
  private String description;
  @SerializedName("facebook_id")
  @Expose
  private String facebookId;
  @SerializedName("followees_count")
  @Expose
  private int followeesCount;
  @SerializedName("followers_count")
  @Expose
  private int followersCount;
  @SerializedName("github_login_name")
  @Expose
  private String githubLoginName;
  @PrimaryKey
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("items_count")
  @Expose
  private int itemsCount;
  @SerializedName("linkedin_id")
  @Expose
  private String linkedinId;
  @SerializedName("location")
  @Expose
  private String location;
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("organization")
  @Expose
  private String organization;
  @SerializedName("permanent_id")
  @Expose
  private long permanentId;
  @SerializedName("profile_image_url")
  @Expose
  private String profileImageUrl;
  @SerializedName("twitter_screen_name")
  @Expose
  private String twitterScreenName;
  @SerializedName("website_url")
  @Expose
  private String websiteUrl;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFacebookId() {
    return facebookId;
  }

  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }

  public int getFolloweesCount() {
    return followeesCount;
  }

  public void setFolloweesCount(int followeesCount) {
    this.followeesCount = followeesCount;
  }

  public int getFollowersCount() {
    return followersCount;
  }

  public void setFollowersCount(int followersCount) {
    this.followersCount = followersCount;
  }

  public String getGithubLoginName() {
    return githubLoginName;
  }

  public void setGithubLoginName(String githubLoginName) {
    this.githubLoginName = githubLoginName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getItemsCount() {
    return itemsCount;
  }

  public void setItemsCount(int itemsCount) {
    this.itemsCount = itemsCount;
  }

  public String getLinkedinId() {
    return linkedinId;
  }

  public void setLinkedinId(String linkedinId) {
    this.linkedinId = linkedinId;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public long getPermanentId() {
    return permanentId;
  }

  public void setPermanentId(long permanentId) {
    this.permanentId = permanentId;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public String getTwitterScreenName() {
    return twitterScreenName;
  }

  public void setTwitterScreenName(String twitterScreenName) {
    this.twitterScreenName = twitterScreenName;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }
}
