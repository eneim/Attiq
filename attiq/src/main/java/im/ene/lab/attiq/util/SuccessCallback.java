package im.ene.lab.attiq.util;

import retrofit2.Callback;

/**
 * Created by eneim on 1/18/16.
 * <p/>
 * Retrofit Callback which cares only success responses
 */
public abstract class SuccessCallback<T> implements Callback<T> {

  @Override public void onFailure(Throwable t) {

  }
}
