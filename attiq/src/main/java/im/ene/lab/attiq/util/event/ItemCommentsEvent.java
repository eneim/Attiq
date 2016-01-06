package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.two.Comment;

import java.util.List;

/**
 * Created by eneim on 12/16/15.
 */
public class ItemCommentsEvent extends Event {

  public final List<Comment> comments;

  public ItemCommentsEvent(boolean isSuccess, @Nullable Error error, List<Comment> comments) {
    super(isSuccess, error);
    this.comments = comments;
  }
}
