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

  // Since RealmObject doesn't support toString()
  public static String toString(FeedItem item) {
    return "item{" +
        ", f_image='" + item.getFollowableImageUrl() + '\'' +
        ", f_name='" + item.getFollowableName() + '\'' +
        ", f_type='" + item.getFollowableType() + '\'' +
        ", f_url='" + item.getFollowableUrl() + '\'' +
        ", m_body='" + item.getMentionedObjectBody() + '\'' +
        ", m_comments=" + item.getMentionedObjectCommentsCount() +
        ", m_image='" + item.getMentionedObjectImageUrl() + '\'' +
        ", m_name='" + item.getMentionedObjectName() + '\'' +
        ", m_stock=" + item.getMentionedObjectStocksCount() +
        ", m_url='" + item.getMentionedObjectUrl() + '\'' +
        ", track='" + item.getTrackableType() + '\'' +
        '}';
  }

  public static int hashCode(FeedItem item) {
    int result = item.getCreatedAtInUnixtime().hashCode();
    result = 31 * result + item.getFollowableImageUrl().hashCode();
    result = 31 * result + item.getFollowableName().hashCode();
    result = 31 * result + item.getFollowableType().hashCode();
    result = 31 * result + item.getFollowableUrl().hashCode();
    result = 31 * result + (item.getMentionedObjectBody() != null ?
        item.getMentionedObjectBody().hashCode() : 0);
    result = 31 * result + (item.getMentionedObjectCommentsCount() != null ?
        item.getMentionedObjectCommentsCount().hashCode() : 0);
    result = 31 * result + (item.getMentionedObjectImageUrl() != null ?
        item.getMentionedObjectImageUrl().hashCode() : 0);
    result = 31 * result + item.getMentionedObjectName().hashCode();
    result = 31 * result + (item.getMentionedObjectStocksCount() != null ?
        item.getMentionedObjectStocksCount().hashCode() : 0);
    result = 31 * result + (item.getMentionedObjectTags() != null ?
        item.getMentionedObjectTags().toString().hashCode() : 0);
    result = 31 * result + item.getMentionedObjectUrl().hashCode();
    result = 31 * result + (item.getMentionedObjectUuid() != null ?
        item.getMentionedObjectUuid().hashCode() : 0);
    result = 31 * result + item.getTrackableType().hashCode();
    return result;
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
