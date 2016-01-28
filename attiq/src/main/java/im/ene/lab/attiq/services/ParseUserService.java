/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
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

package im.ene.lab.attiq.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import im.ene.lab.attiq.BuildConfig;
import im.ene.lab.attiq.data.model.two.Profile;
import im.ene.lab.attiq.util.IOUtil;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by eneim on 1/23/16.
 */
public class ParseUserService extends IntentService {

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   */
  public ParseUserService() {
    super("ParseUserService");
  }

  @Override protected void onHandleIntent(Intent intent) {
    Bundle params = intent.getExtras();
    if (params == null) {
      return;
    }

    String userName = params.getString(Argument.USER_NAME);
    String password = params.getString(Argument.PASSWORD);
    String qiitaUserName = params.getString(Argument.QIITA_USER_NAME);
    String token = params.getString(Argument.ATTIQ_TOKEN);

    if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
      /* Current user is anonymous, so we update it with cloud */
      // check if expected username is already existed on cloud.
      ParseUser onlineUser = null;
      try {
        assert userName != null : "Username is null";
        assert password != null : "Password is null";
        onlineUser = ParseUser.logIn(userName, password);
      } catch (ParseException e) {
        e.printStackTrace();
      }

      // failed to login, therefore we need to sign up
      if (onlineUser == null) {
        onlineUser = ParseUser.getCurrentUser();
        onlineUser.setUsername(userName);
        onlineUser.setPassword(password);
        onlineUser.put("qiitaUserName", qiitaUserName);
        onlineUser.put("attiqToken", token);
        try {
          onlineUser.signUp();
        } catch (ParseException e) {
          e.printStackTrace();
        }
      } else {
        // logged in, so update
        onlineUser.put("qiitaUserName", qiitaUserName);
        onlineUser.put("attiqToken", token);
        try {
          onlineUser.save();
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }
    } else {
      // the user is not anonymous, meaning that current user is already registered to cloud
      // we update some fields (these fields are changeable)
      if (userName != null && userName.equals(ParseUser.getCurrentUser().getUsername())) {
        ParseUser.getCurrentUser().put("qiitaUserName", qiitaUserName);
        ParseUser.getCurrentUser().put("attiqToken", token);
        try {
          ParseUser.getCurrentUser().save();
        } catch (ParseException e) {
          e.printStackTrace();
        }
      } else {
        if (BuildConfig.DEBUG) {
          throw new RuntimeException("Undefined user");
        }
      }
    }
  }

  public static class Argument {

    private static final String USER_NAME = "auth_params_username";
    private static final String PASSWORD = "auth_params_password";
    private static final String QIITA_USER_NAME = "auth_params_qitta_username";
    private static final String ATTIQ_TOKEN = "auth_params_attiq_token";

    private final Bundle arguments;

    public Argument(Profile profile) {
      this.arguments = new Bundle();
      this.arguments.putString(USER_NAME, String.valueOf(profile.getPermanentId()));
      this.arguments.putString(QIITA_USER_NAME, profile.getId());
      this.arguments.putString(ATTIQ_TOKEN, profile.getToken());
      try {
        this.arguments.putString(PASSWORD, IOUtil.sha1(String.valueOf(profile.getPermanentId())));
      } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }

    public Bundle getArguments() {
      return this.arguments;
    }
  }
}
