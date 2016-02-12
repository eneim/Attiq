package im.ene.lab.attiq.util.event;

import android.support.annotation.Nullable;

import im.ene.lab.attiq.data.model.two.Comment;

import java.util.List;

/**
 * Created by eneim on 12/16/15.
 */
public class ItemCommentsEvent extends Event {

  public final List<Comment> comments;

  public ItemCommentsEvent(boolean isSuccess, @Nullable Error error, List<Comment> comments) {
    this(ItemCommentsEvent.class.getSimpleName(), isSuccess, error, comments);
  }

  public ItemCommentsEvent(@Nullable String tag, boolean success, @Nullable Error error,
                           List<Comment> comments) {
    super(tag, success, error);
    this.comments = comments;
  }
}
