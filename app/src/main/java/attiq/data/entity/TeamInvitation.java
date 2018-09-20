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

/**
 * Invited member
 * <p>
 * Represents members who are invited to on Qiita:Team (only available on Qiita:Team).
 */
public class TeamInvitation {

  /**
   * Email address of the invited member
   * (Required)
   */
  @Json(name = "email") private String email;
  /**
   * Invitation URL. The expiration date is one day.
   * (Required)
   */
  @Json(name = "url") private String url;

  /**
   * Email address of the invited member
   * (Required)
   */
  public String getEmail() {
    return email;
  }

  /**
   * Email address of the invited member
   * (Required)
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Invitation URL. The expiration date is one day.
   * (Required)
   */
  public String getUrl() {
    return url;
  }

  /**
   * Invitation URL. The expiration date is one day.
   * (Required)
   */
  public void setUrl(String url) {
    this.url = url;
  }
}
