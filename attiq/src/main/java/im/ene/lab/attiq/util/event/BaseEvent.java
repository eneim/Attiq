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

import android.support.annotation.Nullable;

/**
 * Created by eneim on 1/26/16.
 */
public class BaseEvent<T> extends Event {

  public final T object;

  public BaseEvent(@Nullable String tag, boolean success, @Nullable Error error, T object) {
    super(tag, success, error);
    this.object = object;
  }

  public static class Builder<T> {

    private final String tag;

    private T object;

    private boolean success;

    private Error error;

    public Builder(String tag) {
      this.tag = tag;
    }

    public Builder object(T object) {
      this.object = object;
      return this;
    }

    public Builder success(boolean success) {
      this.success = success;
      return this;
    }

    public Builder error(Error error) {
      this.error = error;
      return this;
    }

    public BaseEvent<T> build() {
      return new BaseEvent<>(this.tag, this.success, this.error, this.object);
    }
  }
}
