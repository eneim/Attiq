package im.ene.lab.attiq.data.api.open.request;

import im.ene.lab.attiq.data.api.base.Request;

/**
 * Created by eneim on 12/28/15.
 */
public class PublicListRequest extends Request {

  public final Long bottomId;

  public PublicListRequest(boolean isLoadingMore, Long bottomId) {
    super(isLoadingMore);
    this.bottomId = bottomId;
  }
}
