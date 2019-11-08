import java.io.File;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chapter6_Synchronizers {
    //CountDownLatch
    private static void testCountDownLatch(){
        //等待一个或多个线程打开一个"门"，即计数器
        //当计数器减为0时，所有线程同时执行
        //await()，阻塞调用此对象的线程，当减为0时，才返回
        //countDown()，等待线程调用此方法对计数器减1

        //三个子线程等待父线程触发同时处理自己的逻辑
        //所有子线程完成后，通过父线程继续往下执行
        int nThreads = 3;
        CountDownLatch startCountDownLatch = new CountDownLatch(1);
        CountDownLatch doneCountDownLatch = new CountDownLatch(nThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        Runnable runnable = ()->{
            System.out.println("子线程【"+Thread.currentThread().getName()+"】 正在等待父线程 触发");
            try {
                startCountDownLatch.await();
                System.out.println("子线程【"+Thread.currentThread().getName()+"】 执行自己的逻辑");
                Thread.sleep(new Random().nextInt(1000));//随机休眠时间
               // doneCountDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        for(int i=0;i<nThreads;i++){
            executorService.execute(runnable);
        }
        System.out.println("父线程执行逻辑。。。。");

        startCountDownLatch.countDown();
        try {
            doneCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("父线程 执行逻辑。。。");
        executorService.shutdown();
    }

    public static void main(String ...args){

        //testCountDownLatch();
        System.out.println(File.pathSeparator);
    }
}
