
package im.ene.lab.attiq.data.vault;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PublicItem extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  @Expose
  private long id;
  @SerializedName("uuid")
  @Expose
  private String uuid;
  @SerializedName("user")
  @Expose
  private PublicUser user;
  @SerializedName("title")
  @Expose
  private String title;
  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @SerializedName("updated_at")
  @Expose
  private String updatedAt;
  @SerializedName("created_at_in_words")
  @Expose
  private String createdAtInWords;
  @SerializedName("updated_at_in_words")
  @Expose
  private String updatedAtInWords;
  @SerializedName("tags")
  @Expose
  private RealmList<PublicTag> tags = new RealmList<>();
  @SerializedName("stock_count")
  @Expose
  private int stockCount;
  @SerializedName("comment_count")
  @Expose
  private int commentCount;
  @SerializedName("url")
  @Expose
  private String url;
  @SerializedName("created_at_as_seconds")
  @Expose
  private int createdAtAsSeconds;
  @SerializedName("tweet")
  @Expose
  private boolean tweet;
  @SerializedName("gist_url")
  @Expose
  private String gistUrl;
  @SerializedName("private")
  @Expose
  private boolean isPrivate;
  @SerializedName("stocked")
  @Expose
  private boolean stocked;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public PublicUser getUser() {
    return user;
  }

  public void setUser(PublicUser user) {
    this.user = user;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCreatedAtInWords() {
    return createdAtInWords;
  }

  public void setCreatedAtInWords(String createdAtInWords) {
    this.createdAtInWords = createdAtInWords;
  }

  public String getUpdatedAtInWords() {
    return updatedAtInWords;
  }

  public void setUpdatedAtInWords(String updatedAtInWords) {
    this.updatedAtInWords = updatedAtInWords;
  }

  public RealmList<PublicTag> getTags() {
    return tags;
  }

  public void setTags(RealmList<PublicTag> tags) {
    this.tags = tags;
  }

  public int getStockCount() {
    return stockCount;
  }

  public void setStockCount(int stockCount) {
    this.stockCount = stockCount;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getCreatedAtAsSeconds() {
    return createdAtAsSeconds;
  }

  public void setCreatedAtAsSeconds(int createdAtAsSeconds) {
    this.createdAtAsSeconds = createdAtAsSeconds;
  }

  public boolean isTweet() {
    return tweet;
  }

  public void setTweet(boolean tweet) {
    this.tweet = tweet;
  }

  public String getGistUrl() {
    return gistUrl;
  }

  public void setGistUrl(String gistUrl) {
    this.gistUrl = gistUrl;
  }

  public boolean isPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public boolean isStocked() {
    return stocked;
  }

  public void setStocked(boolean stocked) {
    this.stocked = stocked;
  }
}
