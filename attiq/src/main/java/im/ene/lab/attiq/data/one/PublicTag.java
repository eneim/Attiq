
package im.ene.lab.attiq.data.one;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PublicTag extends RealmObject {

  @PrimaryKey
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("url_name")
  @Expose
  private String urlName;
  @SerializedName("icon_url")
  @Expose
  private String iconUrl;
  @SerializedName("following")
  @Expose
  private boolean following;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public boolean isFollowing() {
    return following;
  }

  public void setFollowing(boolean following) {
    this.following = following;
  }
}
