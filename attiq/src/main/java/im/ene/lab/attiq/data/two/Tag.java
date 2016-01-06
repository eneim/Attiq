package im.ene.lab.attiq.data.two;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by eneim on 12/21/15.
 */
public class Tag extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  @Expose
  private String id;

  @SerializedName("followers_count")
  @Expose
  private String followersCount;

  @SerializedName("icon_url")
  @Expose
  private String iconUrl;

  @SerializedName("items_count")
  @Expose
  private String itemsCount;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFollowersCount() {
    return followersCount;
  }

  public void setFollowersCount(String followersCount) {
    this.followersCount = followersCount;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public String getItemsCount() {
    return itemsCount;
  }

  public void setItemsCount(String itemsCount) {
    this.itemsCount = itemsCount;
  }
}
