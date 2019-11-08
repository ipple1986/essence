package loadbalance;

public interface LoadBalance {
    Invoker select();
}
