package im.ene.lab.attiq.data;

import io.realm.Realm;
import io.realm.RealmObject;

import java.util.List;

/**
 * Created by eneim on 1/9/16.
 */
public class ListTransaction<E extends RealmObject> implements Realm.Transaction {

  private List<E> items;

  public ListTransaction(List<E> items) {
    this.items = items;
  }

  @Override public void execute(Realm realm) {
    realm.copyToRealmOrUpdate(items);
  }
}
