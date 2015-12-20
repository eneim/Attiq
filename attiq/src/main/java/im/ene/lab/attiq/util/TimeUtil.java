package im.ene.lab.attiq.util;

import android.app.Application;
import android.support.annotation.NonNull;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.ocpsoft.prettytime.PrettyTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class TimeUtil {

  private static final PrettyTime PRETTY_TIME = new PrettyTime();

  private static final DateTimeFormatter API_V2_TIME_FORMAT =
      DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  private static final DateTimeFormatter YYYY_M_DD_EEE_HH_mm =
      DateTimeFormatter.ofPattern("YYYY年M月dd日(EEE) HH:mm", Locale.getDefault());
  private static final DateTimeFormatter M_DD_EEE_HH_mm =
      DateTimeFormatter.ofPattern("M月dd日(EEE) HH:mm", Locale.getDefault());

  private TimeUtil() {
    throw new AssertionError("Illegal");
  }

  public static void init(Application application) {
    AndroidThreeTen.init(application);
  }

  public static String commentTime(String time) {
    ZonedDateTime parsedTime = ZonedDateTime.parse(time, API_V2_TIME_FORMAT);
    // int thisYear = thisYear();
    // if (thisYear == parsedTime.getYear()) {
    //   return parsedTime.format(M_DD_EEE_HH_mm);
    // }
    return parsedTime.format(YYYY_M_DD_EEE_HH_mm);
  }

  private static int thisYear() {
    return ZonedDateTime.now(ZoneId.systemDefault()).getYear();
  }

  public static String beautify(@NonNull String apiV2TimeString) {
    // convert to second then beatify the result
    return beautify(ZonedDateTime.parse(apiV2TimeString, API_V2_TIME_FORMAT).toEpochSecond());
  }

  public static String beautify(long second) {
    return beautify(second, TimeUnit.SECONDS);
  }

  private static String beautify(long time, @NonNull TimeUnit unit) {
    return PRETTY_TIME.format(new Date(unit.toMillis(time)));
  }
}
