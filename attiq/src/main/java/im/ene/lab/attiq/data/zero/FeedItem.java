package im.ene.lab.attiq.data.zero;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import im.ene.lab.attiq.data.one.PublicTag;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by eneim on 12/25/15.
 */
public class FeedItem extends RealmObject {

  public static final String FOLLOWABLE_TYPE_TAG = "Tag";

  public static final String FOLLOWABLE_TYPE_USER = "User";

  public static final String TRACKABLE_TYPE_PUBLIC = "PublicDomainArticle"; // someone posts smthg

  public static final String TRACKABLE_TYPE_STOCK = "StockItem";  // someone stocks smthg

  public static final String TRACKABLE_TYPE_TAG = "Tagging";  // sometag has some new posts

  public static final String TRACKABLE_TYPE_FOLLOW_TAG = "TagFollowlist"; // follow new post

  public static final String TRACKABLE_TYPE_FOLLOW_USER = "FollowingUser";  // follow new user

  public static final String TRACKABLE_TYPE_COMMENT = "Comment";  // comment on post

  @SerializedName("created_at_in_unixtime")
  @Expose
  private Long createdAtInUnixtime;
  @SerializedName("created_at_in_words")
  @Expose
  private String createdAtInWords;
  @SerializedName("followable_image_url")
  @Expose
  private String followableImageUrl;
  @SerializedName("followable_name")
  @Expose
  private String followableName;
  @SerializedName("followable_type")
  @Expose
  private String followableType;
  @SerializedName("followable_url")
  @Expose
  private String followableUrl;
  @SerializedName("mentioned_object_body")
  @Expose
  private String mentionedObjectBody;
  @SerializedName("mentioned_object_comments_count")
  @Expose
  private Integer mentionedObjectCommentsCount;
  @SerializedName("mentioned_object_image_url")
  @Expose
  private String mentionedObjectImageUrl;
  @SerializedName("mentioned_object_name")
  @Expose
  private String mentionedObjectName;
  @SerializedName("mentioned_object_stocks_count")
  @Expose
  private Integer mentionedObjectStocksCount;
  @SerializedName("mentioned_object_tags")
  @Expose
  private RealmList<PublicTag> mentionedObjectTags = new RealmList<>();
  @SerializedName("mentioned_object_url")
  @Expose
  private String mentionedObjectUrl;
  @PrimaryKey
  @SerializedName("mentioned_object_uuid")
  @Expose
  private String mentionedObjectUuid;
  @SerializedName("trackable_type")
  @Expose
  private String trackableType;

  public Long getCreatedAtInUnixtime() {
    return createdAtInUnixtime;
  }

  public void setCreatedAtInUnixtime(Long createdAtInUnixtime) {
    this.createdAtInUnixtime = createdAtInUnixtime;
  }

  public String getCreatedAtInWords() {
    return createdAtInWords;
  }

  public void setCreatedAtInWords(String createdAtInWords) {
    this.createdAtInWords = createdAtInWords;
  }

  public String getFollowableImageUrl() {
    return followableImageUrl;
  }

  public void setFollowableImageUrl(String followableImageUrl) {
    this.followableImageUrl = followableImageUrl;
  }

  public String getFollowableName() {
    return followableName;
  }

  public void setFollowableName(String followableName) {
    this.followableName = followableName;
  }

  public String getFollowableType() {
    return followableType;
  }

  public void setFollowableType(String followableType) {
    this.followableType = followableType;
  }

  public String getFollowableUrl() {
    return followableUrl;
  }

  public void setFollowableUrl(String followableUrl) {
    this.followableUrl = followableUrl;
  }

  public String getMentionedObjectBody() {
    return mentionedObjectBody;
  }

  public void setMentionedObjectBody(String mentionedObjectBody) {
    this.mentionedObjectBody = mentionedObjectBody;
  }

  public Integer getMentionedObjectCommentsCount() {
    return mentionedObjectCommentsCount;
  }

  public void setMentionedObjectCommentsCount(Integer mentionedObjectCommentsCount) {
    this.mentionedObjectCommentsCount = mentionedObjectCommentsCount;
  }

  public String getMentionedObjectImageUrl() {
    return mentionedObjectImageUrl;
  }

  public void setMentionedObjectImageUrl(String mentionedObjectImageUrl) {
    this.mentionedObjectImageUrl = mentionedObjectImageUrl;
  }

  public String getMentionedObjectName() {
    return mentionedObjectName;
  }

  public void setMentionedObjectName(String mentionedObjectName) {
    this.mentionedObjectName = mentionedObjectName;
  }

  public Integer getMentionedObjectStocksCount() {
    return mentionedObjectStocksCount;
  }

  public void setMentionedObjectStocksCount(Integer mentionedObjectStocksCount) {
    this.mentionedObjectStocksCount = mentionedObjectStocksCount;
  }

  public RealmList<PublicTag> getMentionedObjectTags() {
    return mentionedObjectTags;
  }

  public void setMentionedObjectTags(RealmList<PublicTag> mentionedObjectTags) {
    this.mentionedObjectTags = mentionedObjectTags;
  }

  public String getMentionedObjectUrl() {
    return mentionedObjectUrl;
  }

  public void setMentionedObjectUrl(String mentionedObjectUrl) {
    this.mentionedObjectUrl = mentionedObjectUrl;
  }

  public String getMentionedObjectUuid() {
    return mentionedObjectUuid;
  }

  public void setMentionedObjectUuid(String mentionedObjectUuid) {
    this.mentionedObjectUuid = mentionedObjectUuid;
  }

  public String getTrackableType() {
    return trackableType;
  }

  public void setTrackableType(String trackableType) {
    this.trackableType = trackableType;
  }

}
