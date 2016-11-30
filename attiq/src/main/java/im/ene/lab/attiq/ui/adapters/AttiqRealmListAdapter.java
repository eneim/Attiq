package im.ene.lab.attiq.ui.adapters;

import io.realm.RealmObject;

/**
 * Created by eneim on 12/13/15.
 *
 * Customized for Attiq, use data from Realm and save data to Realm
 */
public abstract class AttiqRealmListAdapter<T extends RealmObject> extends AttiqListAdapter<T> {
  // Force data of this Adapter be a RealmObject

  public AttiqRealmListAdapter() {
    super();
    setHasStableIds(true);
  }
}
