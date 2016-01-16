package im.ene.lab.attiq.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.res.AssetManager;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.data.zero.FeedItem;
import io.realm.RealmObject;
import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by eneim on 12/13/15.
 */
public class IOUtil {

  private static final Gson GSON;

  static {
    GSON = new GsonBuilder()
        .setExclusionStrategies(new ExclusionStrategy() {
          @Override
          public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaringClass().equals(RealmObject.class);
          }

          @Override
          public boolean shouldSkipClass(Class<?> clazz) {
            return false;
          }
        })
        .excludeFieldsWithoutExposeAnnotation().create();
  }

  public static Gson gson() {
    return GSON;
  }

  public static String readRaw(int rawFileId) throws IOException {
    InputStream stream = Attiq.creator().getResources().openRawResource(rawFileId);
    BufferedSource buffer = Okio.buffer(Okio.source(stream));
    return buffer.readString(Charset.forName("utf-8"));
  }

  public static String readAssets(String fileName) throws IOException {
    AssetManager assetManager = Attiq.creator().getAssets();
    InputStream stream = assetManager.open(fileName);
    BufferedSource buffer = Okio.buffer(Okio.source(stream));
    return buffer.readString(Charset.forName("utf-8"));
  }

  public static String readAssetFolder(String folder) throws IOException {
    final StringBuilder stringBuilder = new StringBuilder();
    AssetManager assetManager = Attiq.creator().getAssets();
    String[] files = assetManager.list(folder);
    if (files != null && files.length > 0) {
      Iterator<String> filesIterator = Arrays.asList(files).iterator();
      String divider = "\n\n---\n\n";
      while (filesIterator.hasNext()) {
        stringBuilder.append(readAssets(folder + "/" + filesIterator.next()));
        if (filesIterator.hasNext()) {
          stringBuilder.append(divider);
        }
      }
    }

    return stringBuilder.toString();
  }

  public static String toString(FeedItem item) {
    return "FeedItem{" +
        "createdAtInUnixtime=" + item.getCreatedAtInUnixtime() +
        ", createdAtInWords='" + item.getCreatedAtInWords() + '\'' +
        ", followableImageUrl='" + item.getFollowableImageUrl() + '\'' +
        ", followableName='" + item.getFollowableName() + '\'' +
        ", followableType='" + item.getFollowableType() + '\'' +
        ", followableUrl='" + item.getFollowableUrl() + '\'' +
        ", mentionedObjectBody='" + item.getMentionedObjectBody() + '\'' +
        ", mentionedObjectCommentsCount=" + item.getMentionedObjectCommentsCount() +
        ", mentionedObjectImageUrl='" + item.getMentionedObjectImageUrl() + '\'' +
        ", mentionedObjectName='" + item.getMentionedObjectName() + '\'' +
        ", mentionedObjectStocksCount=" + item.getMentionedObjectStocksCount() +
        ", mentionedObjectUrl='" + item.getMentionedObjectUrl() + '\'' +
        // ", mentionedObjectUuid='" + item.getMentionedObjectUuid() + '\'' +
        ", trackableType='" + item.getTrackableType() + '\'' +
        '}';
  }

  public static String sha1(String text) throws NoSuchAlgorithmException,
      UnsupportedEncodingException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    byte[] bytes = text.getBytes("UTF-8");
    digest.update(bytes, 0, bytes.length);
    bytes = digest.digest();
    // This is ~55x faster than looping and String.formating()
    return bytesToHex(bytes);
  }

  final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

  private static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }
}
