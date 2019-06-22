import java.util.concurrent.*;

public class Chapter5_Executors {
    //线程池
    //Executors.newFixedThreadPool(int nThreads) nThreads Threads + 共享无界队列
    //好好研究一下这些类
    public static void main(String... args){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<Integer> callable  = ()->{
            int result = 0;
            for(int i=1;i<100;i++){
                result += i*i;
            }
            return result;
        };

        Future<Integer> future = executorService.submit(callable);
        while(!future.isDone())System.out.println("等待计算结果....");
        try {
            System.out.println("结果 :"+future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdownNow();
    }
}
