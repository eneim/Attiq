package im.ene.lab.attiq.data.event;

import im.ene.lab.attiq.data.Master;

/**
 * Created by eneim on 12/14/15.
 */
public class FetchedMasterEvent extends Event {

  private Master master;

  public FetchedMasterEvent(boolean isSuccess, Master master) {
    super(isSuccess);
    this.master = master;
  }

  public Master getMaster() {
    return master;
  }

  public void setMaster(Master master) {
    this.master = master;
  }
}
