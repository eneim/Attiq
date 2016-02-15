package im.ene.lab.attiq.data.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eneim on 1/11/16.
 */
public class QiitaError {

  @SerializedName("type")
  @Expose
  public String type;

  @SerializedName("message")
  @Expose
  public String message;

  QiitaError(String type, String message) {
    this.type = type;
    this.message = message;
  }

  public static QiitaError unknown() {
    return new QiitaError("unknown", "Unknown Error");
  }

}
