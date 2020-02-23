package loadbalance;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalanceTest {
    @Test
    public void test() {

/*        List<Invoker> invokers = new ArrayList<>();
        invokers.add(new NodeInvoker("node1",Boolean.TRUE,"127.0.0.1",8080));
        invokers.add(new NodeInvoker("node2",Boolean.TRUE,"127.0.0.1",8180));

        RobbinLoadBalance robbinLoadBalance = new RobbinLoadBalance();
        robbinLoadBalance.setInvokers(invokers);
        System.out.println(robbinLoadBalance.select());

        RandomLoadBalance randomLoadBalance = new RandomLoadBalance();
        randomLoadBalance.setInvokers(invokers);
        System.out.println(randomLoadBalance.select());*/
        Caller caller = new Caller();
        int n=120;
        ExecutorService executorService = Executors.newFixedThreadPool(n);
        CountDownLatch countDownLatch = new CountDownLatch(n);
        long start = System.currentTimeMillis();
        for(int i=0;i<n;i++){
            executorService.execute(()->{
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                caller.call();
            });
            countDownLatch.countDown();
        }
        executorService.shutdown();
        while (!executorService.isTerminated());
        System.out.println(System.currentTimeMillis()-start);

    }
}
