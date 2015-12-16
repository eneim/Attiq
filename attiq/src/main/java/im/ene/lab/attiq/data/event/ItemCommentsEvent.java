package im.ene.lab.attiq.data.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.response.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 12/16/15.
 */
public class ItemCommentsEvent extends Event {

  private List<Comment> comments = new ArrayList<>();

  public ItemCommentsEvent(boolean isSuccess, @Nullable Error error, List<Comment> comments) {
    super(isSuccess, error);
    this.comments = comments;
  }

  public List<Comment> getComments() {
    return comments;
  }
}
