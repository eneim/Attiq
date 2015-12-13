package im.ene.lab.attiq.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by eneim on 12/13/15.
 */
public class Item extends RealmObject {

  @SerializedName("rendered_body")
  @Expose
  private String renderedBody;
  @SerializedName("body")
  @Expose
  private String body;
  @SerializedName("coediting")
  @Expose
  private Boolean coediting;
  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @PrimaryKey
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("private")
  @Expose
  private Boolean isPrivate;
  @SerializedName("tags")
  @Expose
  private RealmList<ItemTag> tags = new RealmList<>();
  @SerializedName("title")
  @Expose
  private String title;
  @SerializedName("updated_at")
  @Expose
  private String updatedAt;
  @SerializedName("url")
  @Expose
  private String url;
  @SerializedName("user")
  @Expose
  private User user;

  public String getRenderedBody() {
    return renderedBody;
  }

  public void setRenderedBody(String renderedBody) {
    this.renderedBody = renderedBody;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Boolean getCoediting() {
    return coediting;
  }

  public void setCoediting(Boolean coediting) {
    this.coediting = coediting;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public RealmList<ItemTag> getTags() {
    return tags;
  }

  public void setTags(RealmList<ItemTag> tags) {
    this.tags = tags;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
