
package im.ene.lab.attiq.data.model.two;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Comment extends RealmObject {

  @SerializedName("body")
  @Expose
  private String body;
  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @PrimaryKey
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("rendered_body")
  @Expose
  private String renderedBody;
  @SerializedName("updated_at")
  @Expose
  private String updatedAt;
  @SerializedName("user")
  @Expose
  private User user;

  // remember where this belongs to
  private String itemId;

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  /**
   * @return The body
   */
  public String getBody() {
    return body;
  }

  /**
   * @param body The body
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return The createdAt
   */
  public String getCreatedAt() {
    return createdAt;
  }

  /**
   * @param createdAt The created_at
   */
  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * @return The id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id The id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return The renderedBody
   */
  public String getRenderedBody() {
    return renderedBody;
  }

  /**
   * @param renderedBody The rendered_body
   */
  public void setRenderedBody(String renderedBody) {
    this.renderedBody = renderedBody;
  }

  /**
   * @return The updatedAt
   */
  public String getUpdatedAt() {
    return updatedAt;
  }

  /**
   * @param updatedAt The updated_at
   */
  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  /**
   * @return The user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user The user
   */
  public void setUser(User user) {
    this.user = user;
  }

}
