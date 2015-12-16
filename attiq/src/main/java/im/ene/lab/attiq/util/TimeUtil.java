package im.ene.lab.attiq.util;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by eneim on 12/17/15.
 */
public class TimeUtil {

  private TimeUtil() {
    throw new AssertionError("Illegal");
  }

  public static void init(Application application) {
    AndroidThreeTen.init(application);
  }

  private static final DateTimeFormatter SOURCE =
      DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  private static final DateTimeFormatter YYYY_M_DD_EEE_HH_mm =
      DateTimeFormatter.ofPattern("YYYY年M月dd日(EEE) HH:mm", Locale.getDefault());

  private static final DateTimeFormatter M_DD_EEE_HH_mm =
      DateTimeFormatter.ofPattern("M月dd日(EEE) HH:mm", Locale.getDefault());

  public static String commentTime(String time) {
    ZonedDateTime parsedTime = ZonedDateTime.parse(time, SOURCE);
    // int thisYear = nowYear();
    // if (thisYear == parsedTime.getYear()) {
    //   return parsedTime.format(M_DD_EEE_HH_mm);
    // }
    return parsedTime.format(YYYY_M_DD_EEE_HH_mm);
  }

  private static int nowYear() {
    return ZonedDateTime.now(ZoneId.systemDefault()).getYear();
  }
}
