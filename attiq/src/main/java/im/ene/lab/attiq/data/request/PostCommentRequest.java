package im.ene.lab.attiq.data.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eneim on 1/18/16.
 */
public class PostCommentRequest {

  @SerializedName("body") //
  @Expose private final String body;

  public PostCommentRequest(String body) {
    this.body = body;
  }

  public String getBody() {
    return body;
  }
}
