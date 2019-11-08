package loadbalance;

public class Caller {
    protected LoadBalance loadBalance = new NodeInvokersBuilder().build();
    public void call(){
        Invoker invoker = loadBalance.select();
        System.out.println(Thread.currentThread().getName()+" "+ invoker);

    }
}
