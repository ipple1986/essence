package singlejvm.mutiplethreads;

import java.util.Random;
import java.util.concurrent.locks.LockSupport;

public class JVM3 {
    public static void main(String[] args) {
        while (true){
            FileLock fileLock = new FileLock();
            Random random = new Random();
            String filePath = "D:\\github\\jim\\study\\file-lock\\src\\main\\resources\\test.lock";
            java.nio.channels.FileLock fl =  fileLock.lock(filePath);
            if(null != fl ){
                int seconds = random.nextInt(10)+10;
                System.out.println("JVM1 "+seconds);
                LockSupport.parkNanos(seconds * 1000_000_000L);
                fileLock.releaseLock(fl);
            }
        }
    }
}
