
package im.ene.lab.attiq.data.one;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserStockItem extends RealmObject {

  @SerializedName("body")
  @Expose
  private String body;
  @SerializedName("comment_count")
  @Expose
  private Integer commentCount;
  @SerializedName("created_at_as_seconds")
  @Expose
  private Integer createdAtAsSeconds;
  @SerializedName("created_at_in_words")
  @Expose
  private String createdAtInWords;
  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @SerializedName("gist_url")
  @Expose
  private String gistUrl;
  @PrimaryKey
  @SerializedName("id")
  @Expose
  private Long id;
  @SerializedName("private")
  @Expose
  private Boolean isPrivate;
  @SerializedName("raw_body")
  @Expose
  private String rawBody;
  @SerializedName("stock_count")
  @Expose
  private Integer stockCount;
//  @SerializedName("stock_users")
//  @Expose
//  private RealmList<PublicUser> stockUsers;
  @SerializedName("stocked")
  @Expose
  private Boolean stocked;
  @SerializedName("tags")
  @Expose
  private RealmList<PublicTag> tags;
  @SerializedName("title")
  @Expose
  private String title;
  @SerializedName("tweet")
  @Expose
  private Boolean tweet;
  @SerializedName("updated_at_in_words")
  @Expose
  private String updatedAtInWords;
  @SerializedName("updated_at")
  @Expose
  private String updatedAt;
  @SerializedName("url")
  @Expose
  private String url;
  @SerializedName("user")
  @Expose
  private PublicUser user;
  @SerializedName("uuid")
  @Expose
  private String uuid;

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Integer getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(Integer commentCount) {
    this.commentCount = commentCount;
  }

  public Integer getCreatedAtAsSeconds() {
    return createdAtAsSeconds;
  }

  public void setCreatedAtAsSeconds(Integer createdAtAsSeconds) {
    this.createdAtAsSeconds = createdAtAsSeconds;
  }

  public String getCreatedAtInWords() {
    return createdAtInWords;
  }

  public void setCreatedAtInWords(String createdAtInWords) {
    this.createdAtInWords = createdAtInWords;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getGistUrl() {
    return gistUrl;
  }

  public void setGistUrl(String gistUrl) {
    this.gistUrl = gistUrl;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public String getRawBody() {
    return rawBody;
  }

  public void setRawBody(String rawBody) {
    this.rawBody = rawBody;
  }

  public Integer getStockCount() {
    return stockCount;
  }

  public void setStockCount(Integer stockCount) {
    this.stockCount = stockCount;
  }

//  public RealmList<PublicUser> getStockUsers() {
//    return stockUsers;
//  }
//
//  public void setStockUsers(RealmList<PublicUser> stockUsers) {
//    this.stockUsers = stockUsers;
//  }

  public Boolean getStocked() {
    return stocked;
  }

  public void setStocked(Boolean stocked) {
    this.stocked = stocked;
  }

  public RealmList<PublicTag> getTags() {
    return tags;
  }

  public void setTags(RealmList<PublicTag> tags) {
    this.tags = tags;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Boolean getTweet() {
    return tweet;
  }

  public void setTweet(Boolean tweet) {
    this.tweet = tweet;
  }

  public String getUpdatedAtInWords() {
    return updatedAtInWords;
  }

  public void setUpdatedAtInWords(String updatedAtInWords) {
    this.updatedAtInWords = updatedAtInWords;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public PublicUser getUser() {
    return user;
  }

  public void setUser(PublicUser user) {
    this.user = user;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

}
