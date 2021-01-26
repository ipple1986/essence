package singlejvm.mutiplethreads;

import java.util.Random;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

public class Test {

    final static FileLock fileLock = new FileLock();
    static Random random = new Random();
    public static void main(String[] args) {
        int T = 1000;
        String filePath = "D:\\github\\jim\\study\\file-lock\\src\\main\\resources\\test.lock";

        int N = 4;
        IntStream.range(1,5).forEach(
                i->new Thread(()->{
                    while (true){
                       java.nio.channels.FileLock fl =  fileLock.lock(filePath);
                       if(null != fl){
                           int seconds = random.nextInt(10)+10;
                           System.out.println(Thread.currentThread().getName()+" "+seconds);
                           LockSupport.parkNanos(seconds * 1000_000_000);
                           fileLock.releaseLock(fl);
                       }
                    }
                },"T"+i).start()
        );

       /* try(FileOutputStream fileInputStream = new FileOutputStream(filePath)){
            FileChannel fileChannel = fileInputStream.getChannel();

            try(FileLock fileLock =  fileChannel.tryLock()){
                if(fileLock!=null){
                    System.out.println(Thread.currentThread().getName());
                    LockSupport.park();
                }
            }catch (OverlappingFileLockException e){

            }
        } catch (IOException e) {
            //e.printStackTrace();
        }*/
// FOR /L %i in (1,1,9) do java singlejvm.mutiplethreads.Test
/*
        CountDownLatch countDownLatch = new CountDownLatch(T);
        CountDownLatch countDownLatch1 = new CountDownLatch(T);
        for(int i=0;i<T;i++){
            new Thread(()->{
                try {
                    try(FileOutputStream fileInputStream = new FileOutputStream(filePath)){
                        FileChannel fileChannel = fileInputStream.getChannel();
                        countDownLatch.await();
                        try(FileLock fileLock =  fileChannel.tryLock()){
                            if(fileLock!=null){
                                System.out.println(Thread.currentThread().getName()+"  get lock");
                                //LockSupport.park();
                            }else{
                                System.out.println(Thread.currentThread().getName()+ " failed");
                            }
                        }catch (OverlappingFileLockException e){

                        }
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }

                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    //System.out.println(Thread.currentThread().getName().concat(" ").concat(e.getMessage()));
                }
            },"T".concat(i+"")).start();
            countDownLatch.countDown();
        }
//        new File(filePath).delete();
    */
    }
}

