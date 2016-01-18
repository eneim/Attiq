package im.ene.lab.attiq.util.markdown;

import android.util.Log;

import org.jsoup.Jsoup;

import java.util.regex.Pattern;

/**
 * Created by eneim on 1/18/16.
 *
 * An improvement for {@link io.github.gitbucket.markedj.Marked}, supposes to support Checkboxes.
 * Please note that Markedj is much slower than other Markdown libraries. But I have no better
 * choices for now.
 */
public class Renderer extends io.github.gitbucket.markedj.Renderer {

  private static final String TAG = "Renderer";

  private final String CHECKBOX_CLOSE_MARK = "[x] ";
  private final String CHECKBOX_OPEN_MARK = "[ ] ";

  public Renderer(Options options) {
    super(options);
  }

  @Override public String listitem(String text) {
    boolean hasCheckbox = false;
    if (text.startsWith(CHECKBOX_CLOSE_MARK)) {
      hasCheckbox = true;
      text = text.replaceFirst(Pattern.quote(CHECKBOX_CLOSE_MARK),
          "<input type=\"checkbox\" class=\"task-list-item-checkbox\" checked=\"\">");
    } else if (text.startsWith(CHECKBOX_OPEN_MARK)) {
      hasCheckbox = true;
      text = text.replaceFirst(Pattern.quote(CHECKBOX_OPEN_MARK),
          "<input type=\"checkbox\" class=\"task-list-item-checkbox\">");
    }
    String listItem = super.listitem(text);
    if (hasCheckbox) {
      listItem = Jsoup.parse(listItem).select("li")
          .attr("class", "task-list-item").first().toString();
      Log.d(TAG, "listitem() called with: " + "text = [" + listItem + "]");
    }

    return listItem;
  }

}
