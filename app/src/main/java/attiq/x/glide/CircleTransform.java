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

package attiq.x.glide;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by http://stackoverflow.com/a/25806229/409481
 *
 * Pulled from /plaidapp/util/glide/CircleTransform.java
 */
public class CircleTransform extends BitmapTransformation {

  private static final String ID = CircleTransform.class.getCanonicalName();

  private static CircleTransform singleton;

  public static CircleTransform getInstance() {
    if (singleton == null) {
      synchronized (CircleTransform.class) {
        if (singleton == null) {
          singleton = new CircleTransform();
        }
      }
    }

    return singleton;
  }

  @SuppressWarnings("WeakerAccess")
  /* package */ CircleTransform() {
  }

  private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
    if (source == null) return null;

    int size = Math.min(source.getWidth(), source.getHeight());
    int x = (source.getWidth() - size) / 2;
    int y = (source.getHeight() - size) / 2;

    Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

    Config config = source.getConfig();
    Bitmap result = pool.get(size, size, config != null ? config : Config.ARGB_8888);

    Canvas canvas = new Canvas(result);
    Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
    paint.setShader(new BitmapShader(squared, TileMode.CLAMP, TileMode.CLAMP));
    float r = size / 2f;
    canvas.drawCircle(r, r, r, paint);
    return result;
  }

  @Override
  protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth,
      int outHeight) {
    return circleCrop(pool, toTransform);
  }

  @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    try {
      messageDigest.update(ID.getBytes(STRING_CHARSET_NAME));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
}
