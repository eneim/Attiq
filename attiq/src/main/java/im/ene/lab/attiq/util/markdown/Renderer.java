package im.ene.lab.attiq.util.markdown;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

/**
 * Created by eneim on 1/18/16.
 * <p/>
 * An improvement for {@link io.github.gitbucket.markedj.Marked}, supposes to support Checkboxes.
 * Please note that Markedj is much slower than other Markdown libraries. But I have no better
 * choices for now.
 */
public class Renderer extends io.github.gitbucket.markedj.Renderer {

  private static final String TAG = "Renderer";

  private static final String CHECKBOX_CLOSE_MARK = "[x] ";
  private static final String CHECKBOX_OPEN_MARK = "[ ] ";

  private static final String CHECKBOX_CLOSE_REPLACEMENT =
      "<input type=\"checkbox\" class=\"task-list-item-checkbox\" " +
          "disabled=\"true\" checked=\"\">";

  private static final String CHECKBOX_OPEN_REPLACEMENT =
      "<input type=\"checkbox\" class=\"task-list-item-checkbox\"" +
          "disabled=\"true\">";

  public Renderer(Options options) {
    super(options);
  }

  /**
   * Support checkbox parsing
   *
   * @param text
   * @return expected list item html code
   */
  @Override public String listitem(String text) {
    boolean hasCheckbox = false;
    String reference = Jsoup.parse(text).text();
    if (reference.startsWith(CHECKBOX_CLOSE_MARK)) {
      hasCheckbox = true;
      text = text.replaceFirst(Pattern.quote(CHECKBOX_CLOSE_MARK), CHECKBOX_CLOSE_REPLACEMENT);
    } else if (reference.startsWith(CHECKBOX_OPEN_MARK)) {
      hasCheckbox = true;
      text = text.replaceFirst(Pattern.quote(CHECKBOX_OPEN_MARK), CHECKBOX_OPEN_REPLACEMENT);
    }

    String listItem = super.listitem(text);
    if (hasCheckbox) {
      Element element = Jsoup.parse(listItem).select("li").attr("class", "task-list-item").first();
      if (element != null) {
        listItem = element.toString();
      }
    }

    return listItem;
  }

}
