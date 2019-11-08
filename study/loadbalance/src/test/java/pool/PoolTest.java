package pool;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class PoolTest {
    @Test
    public void testJRedisPool(){

        int N = 500;
        CountDownLatch countDownLatch = new CountDownLatch(10);

        JRedisPool jRedisPool = new JRedisPool();
        jRedisPool.init(20,2000l);

        for(int i=0;i<N;i++){
            new Thread(()->{
                JRedis jRedis = null;
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    jRedis = jRedisPool.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jRedis.incr("jredis.incr");
                jRedisPool.release(jRedis);
            }).start();
            countDownLatch.countDown();
        }

    }
}
