package pool;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JRedisPool implements Pool<JRedis>{
    private int maxConnections;
    private long maxWaitMillis;
    // 空闲资源数
    private LinkedBlockingQueue<JRedis> idles = null;
    // 活动资源数
    private LinkedBlockingQueue<JRedis> actives = null;
    // 总资源数
    private AtomicInteger totals = new AtomicInteger(0);
    @Override
    public void init(int maxConnections, long maxWaitMillis) {
        this.maxConnections = maxConnections;
        this.maxWaitMillis = maxWaitMillis;
        this.idles = new LinkedBlockingQueue<>(maxConnections);
        this.actives = new LinkedBlockingQueue<>(maxConnections);
    }

    @Override
    public JRedis get() throws Exception {
        // 1。从空闲取出，放入活动 并返回
        // 2。连接池未满时，创建资源放入活动
        // 3。连接池已满，等待有限时间内，从空闲->活动
        // 4。等待最长等待时间，抛出异常
       long startTime = System.currentTimeMillis();
       JRedis jRedis = null;
       while (jRedis==null){
           jRedis = idles.poll();
           if(jRedis!=null){
               actives.offer(jRedis);
               return jRedis;
           }
            if(totals.incrementAndGet() <= maxConnections){
                jRedis = new JRedis();
                System.out.println("----创建 JRedis 资源---------");
                actives.offer(jRedis);
                return jRedis;
            }else{
                totals.decrementAndGet();
            }

           try{
               jRedis =idles.poll(maxWaitMillis - (System.currentTimeMillis()-startTime), TimeUnit.MILLISECONDS);
               if(jRedis!=null){
                   actives.offer(jRedis);
                   return  jRedis;
               }
           }catch (InterruptedException ie){
               ie.printStackTrace();
           }

           if(maxWaitMillis != -1 && maxWaitMillis < (System.currentTimeMillis()-startTime)){
               throw  new Exception("timeout");
           }
       }
       return jRedis;
    }

    @Override
    public void release(JRedis jRedis) {
        if(actives.remove(jRedis)){
            idles.offer(jRedis);
        }
    }
}
