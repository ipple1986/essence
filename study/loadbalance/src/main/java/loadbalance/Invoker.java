package loadbalance;

public interface Invoker {
    boolean invoke();
    boolean isUp();
}
