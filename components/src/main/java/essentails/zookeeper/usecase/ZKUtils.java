package essentails.zookeeper.usecase;

import java.io.IOException;

public abstract class ZKUtils {

    public static final void withoutExitJVM(){
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static final  void threadSleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
