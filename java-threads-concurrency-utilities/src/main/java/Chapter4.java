import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class Chapter4 {
    //线程组/本地线程/定时器框架

    private static void testThreadGroup(){
        //线程组包含线程+子线程组
        //不要使用线程组的suspend,resume,stop方法，易造成死锁等问题
        //线程组 本身就是一个异常处理器，实现Thread.UncaughtExceptionHandler接口//???jdk找不到默认异常处理器

        //* 1。线程run抛出异常，如果当前线程UncaughtExceptionHandler，则交由它处理
        //* 2。如果没有设置，找当前线程的线程组TreadGroup的UncaughtExceptionHandler
        //* 3。如果线程级没设置UncaughtExceptionHandler，交由它的父线程组处理
        //* 4。如果父线程组也没设置UncaughtExceptionHandler，交由当前线程的默认DefaultUncaughtExceptionHandler
        //* 5。如果当前线程没设置默认DefaultUncaughtExceptionHandler,则判断uncaughtException(Thread t, Throwable e)第二个参数e
        //是否为java.lang.ThreadDeath,如果不是就打印也异常栈

        //无异常处理器
        /*
        Thread thread1 = new Thread(()->{int a = 10/0;},"测试线程1");
        thread1.start();
        */
        //只设置当前线程 异常处理器
        Thread thread2 = new Thread(()->{ int a = 10/0; },"测试线程2");
        thread2.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("当前线程的UncaughtExceptionHandler ，捕获到 线程:"+t.getName()+" 中的异常: "+ e );
            }
        });
        thread2.start();
        //只添加当前线程的线程组 异常处理器（当前线程+线程组）
        ThreadGroup threadGroup1 = new ThreadGroup("线程组名称1"){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("当前线程的线程组（"+t.getThreadGroup().getName()+") ，捕获到 线程:"+t.getName()+" 中的异常: "+ e );
            }
        };
        Thread thread3 = new Thread(threadGroup1,()->{ int a = 10/0; },"测试线程3");
        thread3.start();
        //只添加当前线程所在线程组的父线程组 异常处理器(当前线程+两级线程组)
        ThreadGroup threadGroup = new ThreadGroup("父亲线程组名称"){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("当前线程线程组的父线程组（"+t.getThreadGroup().getParent().getName()+") ，捕获到 线程:"+t.getName()+" 中的异常: "+ e );
            }
        };
        ThreadGroup threadGroup2 = new ThreadGroup(threadGroup,"子线程组名称");
        Thread thread4 = new Thread(threadGroup2,()->{ int a = 10/0; },"测试线程4");
        thread4.start();
        //三组线程组呢,也处理
        ThreadGroup threadGroup3 = new ThreadGroup("父亲线程组名称（三级）"){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("当前线程线程组的父线程组（"+t.getThreadGroup().getParent().getParent().getName()+") ，捕获到 线程:"+t.getName()+" 中的异常: "+ e );
            }
        };
        ThreadGroup threadGroup4 = new ThreadGroup(threadGroup3,"一级子线程组名称");
        ThreadGroup threadGroup5 = new ThreadGroup(threadGroup4,"二级子线程组名称");
        Thread thread5 = new Thread(threadGroup5,()->{ int a = 10/0; },"测试线程5");
        thread5.start();
        //*** 同时设置线程的异常处理器跟默认异常处理器，会怎么样？

        Thread thread6 = new Thread(threadGroup5,()->{ int a = 10/0; },"测试线程6");
        thread6.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("当前线程的UncaughtExceptionHandler ，捕获到 线程:"+t.getName()+" 中的异常: "+ e );
            }
        });
        thread6.start();
    }
    static volatile ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(()-> 1 );
    static final InheritableThreadLocal<Integer> inheritableThreadLocal = new InheritableThreadLocal<Integer>(){
         protected Integer childValue(Integer parentValue) { //将父线程的值传给子线程
            return parentValue;
            //此处不修改，父线程没设值，子线程inheritableThreadLocal.get() 等于null
             //此处修改成10,父线程没设值，子线程还是null
             //此处改成10,父线程设置值无论是什么值 ，子线程都拿到10
             //*** 说明当父线程设置时才调用此方法
        }
    };
    private static void testThreadLocal(){

        Runnable runnable = ()->{
            System.out.println("线程"+Thread.currentThread().getName()+"第一次 ThreadLocal.get()时:"+threadLocal.get());
            if(Thread.currentThread().getName().equals("A")){
                threadLocal.set(5);
            }
            if(Thread.currentThread().getName().equals("B")){
                threadLocal.set(10);
            }
            System.out.println("线程"+Thread.currentThread().getName()+" ThreadLocal.get():"+threadLocal.get());
        };
        new Thread(runnable,"A").start();
        new Thread(runnable,"B").start();
        /*
线程A第一次 ThreadLocal.get()时:1
线程B第一次 ThreadLocal.get()时:1
线程B ThreadLocal.get():10
线程A ThreadLocal.get():5
         */
        //继承的ThreadLocal
        new Thread(()-> {
           inheritableThreadLocal.set(11);
           new Thread(()->{
              System.out.println(String.format("从子类获取值 ：%d",inheritableThreadLocal.get()));
           }).start();
        }).start();
    }
    private static void testTimerAndTimeTask(){
        Timer timer = new Timer();//非守护进程
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hello Timer");
                System.exit(0);//非守护进程，让系统正常关闭
            }
        };
        timer.schedule(timerTask,1000);
        //timer.schedule(timerTask,0,1000);
    }
    public static void main(String args[]){
        System.out.println("=====测试线程组=============================");
        testThreadGroup();
        System.out.println("=====测试本地线程=============================");
        testThreadLocal();
        System.out.println("=====测试Timer定时器框架=============================");
        testTimerAndTimeTask();
    }
}
