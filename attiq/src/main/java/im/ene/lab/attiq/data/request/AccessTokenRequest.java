package im.ene.lab.attiq.data.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eneim on 12/13/15.
 */
public class AccessTokenRequest extends Request {

  @SerializedName("client_id")
  @Expose
  private String clientId;

  @SerializedName("client_secret")
  @Expose
  private String clientSecret;

  @SerializedName("code")
  @Expose
  private String code;

  public AccessTokenRequest(boolean isLoadingMore, String clientId, String clientSecret, String
      code) {
    super(isLoadingMore);
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.code = code;
  }
}
