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

package im.ene.lab.attiq.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eneim on 1/27/16.
 * <p/>
 * Type safe screen definitions for Google Analytics
 */
public enum Screen {

  USER_TIMELINE("attiq:user:timeline"),

  USER_FEED("attiq:user:feed"),

  USER_STOCK("attiq:user:stock"),

  PUBLIC("attiq:public:timeline");

  private final String resource;

  // cache screen names
  private static final Map<String, Screen> valuesByName;

  static {
    valuesByName = new HashMap<>();
    for (Screen screen : Screen.values()) {
      valuesByName.put(screen.resource, screen);
    }
  }

  Screen(String resource) {
    this.resource = resource;
  }

  public static Screen lookup(String name) {
    return valuesByName.get(name);
  }

  public String getResource() {
    return resource;
  }
}
