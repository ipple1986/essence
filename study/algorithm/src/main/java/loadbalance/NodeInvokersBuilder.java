package loadbalance;

import java.util.ArrayList;
import java.util.List;

public class NodeInvokersBuilder {
    public LoadBalance build(){
        List<Invoker> invokers = new ArrayList<>();
        invokers.add(new NodeInvoker("node1",Boolean.TRUE,"127.0.0.1",8080));
        invokers.add(new NodeInvoker("node2",Boolean.TRUE,"localhost",8080));
/*        invokers.add(new NodeInvoker("node3",Boolean.TRUE,"127.0.0.1",8080));
        invokers.add(new NodeInvoker("node4",Boolean.TRUE,"127.0.0.1",8080));*/

        //RandomLoadBalance loadBalance = new RandomLoadBalance();
        RobbinLoadBalance loadBalance = new RobbinLoadBalance();
        loadBalance.setInvokers(invokers);
        return  loadBalance;
    }
}
