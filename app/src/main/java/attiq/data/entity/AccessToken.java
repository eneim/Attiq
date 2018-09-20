/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package attiq.data.entity;

import com.squareup.moshi.Json;
import java.util.List;

/**
 * Access token
 * <p>
 * Access token for Qiita API v2
 */
public class AccessToken {

  /**
   * An unique ID to identify a registered client
   * (Required)
   */
  @Json(name = "client_id") private String clientId;
  /**
   * Authorized action scopes of the access token
   * (Required)
   */
  @Json(name = "scopes") private List<String> scopes = null;
  /**
   * Access token identifier string
   * (Required)
   */
  @Json(name = "token") private String token;

  /**
   * An unique ID to identify a registered client
   * (Required)
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * An unique ID to identify a registered client
   * (Required)
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Authorized action scopes of the access token
   * (Required)
   */
  public List<String> getScopes() {
    return scopes;
  }

  /**
   * Authorized action scopes of the access token
   * (Required)
   */
  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  /**
   * Access token identifier string
   * (Required)
   */
  public String getToken() {
    return token;
  }

  /**
   * Access token identifier string
   * (Required)
   */
  public void setToken(String token) {
    this.token = token;
  }
}
