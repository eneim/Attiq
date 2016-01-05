package im.ene.lab.attiq.data.api.v1.request;

import im.ene.lab.attiq.data.api.base.Request;

/**
 * Created by eneim on 12/21/15.
 */
public abstract class V1Request extends Request {

  public V1Request(boolean isLoadingMore) {
    super(isLoadingMore);
  }
}
