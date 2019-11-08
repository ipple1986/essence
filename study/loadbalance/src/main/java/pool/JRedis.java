package pool;

import java.util.Random;

public class JRedis {
    private static Random random = new Random();
    public void incr(String cmd){
        //System.out.println("execute " + cmd);
        try {
            Thread.sleep(1000l*random.nextInt(320000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void release(){}
}
