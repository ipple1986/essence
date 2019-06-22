import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockSupportTest {
   private static void test(){
      // BlockingQueue
       class A {
           private boolean bb (){
               LockSupport.park(Thread.currentThread());
               return Thread.interrupted();
           }
           private  boolean xx() throws InterruptedException{
               try {
                   for (; ; ) {
                       System.out.println("-----------a---");

                       if(bb()){
                           throw  new InterruptedException();
                       }
                       if(true)return Boolean.TRUE;
                       System.out.println("------------b--");
                   }
               } catch (InterruptedException e) {
                   e.printStackTrace();
                   throw new InterruptedException();
               }finally {
                   System.out.println("finally");
               }
           }

       }
       Thread thread =  new Thread(){
           @Override
           public void run() {

               try {
                   System.out.println( new A().xx());
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       };
       thread.start();

//       thread.interrupt();
LockSupport.unpark(thread);
   }

    public static void main(String ...args) throws InterruptedException {
        /*class Sync{
                public final  void done(final String threadName){
                    if("T3".equals(threadName)){
                        concurrent.LockSupport.park(this);
                    }
                }
        }
        Sync  lock = new Sync();
        for(int i=1;i<11;i++){
            new Thread("T" + i){

                @Override
                public void run() {
                    String threadName = Thread.currentThread().getName();
                    lock.done(threadName);
                    System.out.println("当前线程名：" + threadName);
                }

            }.start();
        }

        ReadWriteLock readWriteLock = new concurrent.ReentrantReadWriteLock();*/
        test();
    }
}
