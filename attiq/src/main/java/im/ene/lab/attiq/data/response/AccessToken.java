package im.ene.lab.attiq.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 12/13/15.
 */
public class AccessToken {

  @SerializedName("client_id")
  @Expose
  private String clientId;

  @SerializedName("token")
  @Expose
  private String token;

  @SerializedName("scopes")
  @Expose
  private List<String> scopes = new ArrayList<>();

  public String getClientId() {
    return clientId;
  }

  public String getToken() {
    return token;
  }

  public List<String> getScopes() {
    return scopes;
  }
}
