
package im.ene.lab.attiq.data.model.one;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PublicUser extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  @Expose
  private int id;
  @SerializedName("url_name")
  @Expose
  private String urlName;
  @SerializedName("profile_image_url")
  @Expose
  private String profileImageUrl;
  @SerializedName("following")
  @Expose
  private boolean following;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public boolean isFollowing() {
    return following;
  }

  public void setFollowing(boolean following) {
    this.following = following;
  }
}
