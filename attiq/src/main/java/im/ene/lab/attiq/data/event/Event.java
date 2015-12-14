package im.ene.lab.attiq.data.event;

/**
 * Created by eneim on 12/13/15.
 */
public class Event {

  private boolean isSuccess;

  public Event(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setIsSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
}
