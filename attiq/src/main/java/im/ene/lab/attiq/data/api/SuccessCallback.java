package im.ene.lab.attiq.data.api;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by eneim on 1/18/16.
 * <p/>
 * Retrofit Callback which cares only success responses
 */
public abstract class SuccessCallback<T> implements Callback<T> {

  @Override public void onFailure(Call<T> call, Throwable t) {

  }
}
