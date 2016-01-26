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

package im.ene.lab.attiq.util.event;

import java.util.WeakHashMap;

/**
 * Created by eneim on 1/26/16.
 */
public class EventUtil {

  // Global LOCK
  private static final Object LOCK = new Object();

  private final String mTag;

  private static EventUtil sInstance;

  private static final WeakHashMap<Object, EventUtil> sInstances = new WeakHashMap<>();

  public static EventUtil init(Object object) {
    if ((sInstance = sInstances.get(object)) == null) {
      sInstance = new EventUtil(object.getClass().getSimpleName());
      sInstances.put(object, sInstance);
    }

    return sInstance;
  }

  public static void shutdown(Object object) {
    synchronized (LOCK) {
      sInstances.remove(object);
    }
  }

  public static void destroy() {
    synchronized (LOCK) {
      sInstances.clear();
    }
  }

  public static EventUtil init(String tag) {
    if ((sInstance = sInstances.get(tag)) == null) {
      sInstance = new EventUtil(tag);
      sInstances.put(tag, sInstance);
    }

    return sInstance;
  }

  private EventUtil(String tag) {
    this.mTag = tag;
  }

  public static <T> BaseEvent.Builder<T> newEvent() {
    if (sInstance == null) {
      throw new IllegalStateException("Not been initialized");
    }

    return new BaseEvent.Builder<>(sInstance.mTag);
  }

}
