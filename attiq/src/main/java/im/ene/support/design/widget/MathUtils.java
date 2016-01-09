package im.ene.support.design.widget;

public class MathUtils {

  public static int constrain(int amount, int low, int high) {
    return amount < low ? low : (amount > high ? high : amount);
  }

  public static float constrain(float amount, float low, float high) {
    return amount < low ? low : (amount > high ? high : amount);
  }
}
