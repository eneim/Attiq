package im.ene.lab.attiq.data.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eneim on 1/11/16.
 */
public class HttpError {

  @SerializedName("type")
  @Expose
  public String type;

  @SerializedName("message")
  @Expose
  public String message;

  HttpError(String type, String message) {
    this.type = type;
    this.message = message;
  }

  public HttpError() {
    this("unknown", "Unknown Error");
  }

}
