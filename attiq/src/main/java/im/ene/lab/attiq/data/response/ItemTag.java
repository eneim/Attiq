package im.ene.lab.attiq.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public class ItemTag extends RealmObject {

  @SerializedName("name")
  @Expose
  private String name;

  // FIXME ignore for now, fix later
  @Ignore
  @SerializedName("versions")
  @Expose
  private List<String> versions = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
