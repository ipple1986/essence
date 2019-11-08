import com.sun.org.apache.bcel.internal.generic.RET;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.*;
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
           private  boolean xx() throws InterruptedException {
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
//LockSupport.unpark(thread);
   }

    public static void main(String ...args) throws InterruptedException {
        /*class Sync{
                public final  void done(final String threadName){
                    if("T3".equals(threadName)){
                        javaapi.java.util.concurrent.locks.LockSupport.park(this);
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

        ReadWriteLock readWriteLock = new javaapi.java.util.concurrent.locks.ReentrantReadWriteLock();*/
     //   test();

/*
        ArrayBlockingQueue<String > arrayBlockingQueue = new ArrayBlockingQueue<>(6,Boolean.TRUE,
                Arrays.asList(new String[]{"AAA","BBB","CCC","DDD","EEE","FFF"}));
        new Thread(()->{
            try {
                System.out.println("begin put");
                arrayBlockingQueue.put("GGG");

                System.out.println("end put");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(4000);
        new Thread(()->{
            try {
                System.out.println("begin take");
                System.out.println(arrayBlockingQueue.take());
                System.out.println("end take");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
*/

/*            new Thread(()->{}).start();
            Thread.sleep(2000);
            new Thread(()->{}).start();*/

    }
}
class Lock{
   public  final Stuff stuff = new Stuff(0);
    public void lock(String threadName){
        final  Stuff stuff1 = this.stuff;
        stuff1.compareAndSetState(stuff1,0,1,threadName);
    }
}

class Stuff{
    public Stuff(int state){
        this.state = state;
    }
    protected    int state;
    static long stateOffset ;
    static Unsafe unsafe ;
    public void setState(int state){
        this.state = state;
    }
    public void compareAndSetState(Stuff stuff,int expect,int value,String threadName){
        boolean result = unsafe.compareAndSwapInt(stuff,stateOffset,expect,value);
        System.out.println(threadName+" "+result);
    }
    private Object condition = new Object();
    public Object getCondition(){
        return  condition;
    }
        static{
            try {
                final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {
                    public Unsafe run() throws Exception {
                        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                        theUnsafe.setAccessible(true);
                        return (Unsafe) theUnsafe.get(null);
                    }
                };
                unsafe = AccessController.doPrivileged(action);
            }
            catch (Exception e){
                throw new RuntimeException("Unable to load unsafe", e);
            }
            try {
                stateOffset = unsafe.objectFieldOffset(Stuff.class.getDeclaredField("state"));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

}