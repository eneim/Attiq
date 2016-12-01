package im.ene.lab.attiq;

import android.content.Context;
import android.net.Uri;
import android.os.StatFs;
import android.support.annotation.NonNull;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;
import java.io.File;
import java.io.IOException;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Created by eneim on 11/28/16.
 */

public final class OkHttp3Downloader implements Downloader {
  private static final String PICASSO_CACHE = "picasso-cache";
  private static final int MIN_DISK_CACHE_SIZE = 5242880;
  private static final int MAX_DISK_CACHE_SIZE = 52428800;
  private final Call.Factory client;
  private final Cache cache;

  private static File defaultCacheDir(Context context) {
    File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
    if (!cache.exists()) {
      cache.mkdirs();
    }

    return cache;
  }

  private static long calculateDiskCacheSize(File dir) {
    long size = 5242880L;

    try {
      StatFs statFs = new StatFs(dir.getAbsolutePath());
      long available = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
      size = available / 50L;
    } catch (IllegalArgumentException var6) {
      ;
    }

    return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
  }

  public static Cache createDefaultCache(Context context) {
    File dir = defaultCacheDir(context);
    return new Cache(dir, calculateDiskCacheSize(dir));
  }

  private static OkHttpClient createOkHttpClient(File cacheDir, long maxSize) {
    return (new OkHttpClient.Builder()).cache(new Cache(cacheDir, maxSize)).build();
  }

  public OkHttp3Downloader(Context context) {
    this(defaultCacheDir(context));
  }

  public OkHttp3Downloader(File cacheDir) {
    this(cacheDir, calculateDiskCacheSize(cacheDir));
  }

  public OkHttp3Downloader(Context context, long maxSize) {
    this(defaultCacheDir(context), maxSize);
  }

  public OkHttp3Downloader(File cacheDir, long maxSize) {
    this(createOkHttpClient(cacheDir, maxSize));
  }

  public OkHttp3Downloader(OkHttpClient client) {
    this.client = client;
    this.cache = client.cache();
  }

  public OkHttp3Downloader(Call.Factory client) {
    this.client = client;
    this.cache = null;
  }

  public Response load(@NonNull Uri uri, int networkPolicy) throws IOException {
    CacheControl cacheControl = null;
    if (networkPolicy != 0) {
      if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
        cacheControl = CacheControl.FORCE_CACHE;
      } else {
        okhttp3.CacheControl.Builder builder = new okhttp3.CacheControl.Builder();
        if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
          builder.noCache();
        }

        if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
          builder.noStore();
        }

        cacheControl = builder.build();
      }
    }

    okhttp3.Request.Builder builder1 = (new okhttp3.Request.Builder()).url(uri.toString());
    if (cacheControl != null) {
      builder1.cacheControl(cacheControl);
    }

    okhttp3.Response response = this.client.newCall(builder1.build()).execute();
    int responseCode = response.code();
    if (responseCode >= 300) {
      response.body().close();
      throw new ResponseException(responseCode + " " + response.message(), networkPolicy,
          responseCode);
    } else {
      boolean fromCache = response.cacheResponse() != null;
      ResponseBody responseBody = response.body();
      return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
    }
  }

  public void shutdown() {
    if (this.cache != null) {
      try {
        this.cache.close();
      } catch (IOException var2) {
        var2.printStackTrace();
      }
    }
  }
}
