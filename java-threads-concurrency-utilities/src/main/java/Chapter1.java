public class Chapter1 {
 //Threads and Runnable
    private static void testCreateThreadWays(){
        //创建线程方式一,runnable
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("你好,我是Runnable1");
            }
        };
        Runnable runnable2 = () -> System.out.println("你好,我是Runnable2");//jdk8
        Thread thread1 = new Thread(runnable1);
        thread1.start();
        Thread thread2 = new Thread(runnable2);
        thread2.start();
        //创建线程方式二，thread
        Thread thread = new Thread(){
            @Override
            public void run() {
                System.out.println("你好,我是Thread，继承了Thread,重写run方法");
            }
        };
    }
    private static void testThreadName(){
        //线程名称
        Runnable runnable1 = () -> System.out.println("你好,我是Runnable1");
        Runnable runnable2 = () -> System.out.println("你好,我是Runnable2");//jdk8

        Thread threadNameTest1 = new Thread(runnable1,"线程名1，线程名称通过Thread构造器");
        System.out.println(threadNameTest1.getName());
        Thread threadNameTest2 = new Thread(runnable2);
        threadNameTest2.setName("线程名2，线程名称通过Thread.setName(name)方法");
        System.out.println(threadNameTest2.getName());
    }
    private static void testThreadIsAlive() throws InterruptedException {
        Thread threadAliveTest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println(threadAliveTest.isAlive());//false: not yet started
        threadAliveTest.start();
        System.out.println("让线程休眠5秒");
        Thread.sleep(1000);
        System.out.println(String.format("1秒后，此时线程是否存活：%b",threadAliveTest.isAlive()));//true: started but not leave run method
        Thread.sleep(6000);
        System.out.println(String.format("6秒后，此时线程是否存活：%b",threadAliveTest.isAlive()));//false: leaved run method
    }
    private static void testThreadExecutionState()throws InterruptedException{
        Thread threadExecState = new Thread();
        System.out.println("=====测试 NEW/RUNNING/TERMINATED==================");
        System.out.println(threadExecState.getState());//NEW
        threadExecState.start();//启动线程
        System.out.println(threadExecState.getState());//RUNNABLE
        Thread.sleep(100);
        System.out.println(threadExecState.getState());//TERMINATED

        //BLOCKED/WAITING/TIME_WAITING 在多线程交互中体现
        //BLOCKED 线程A，线程B 同时竞争一个资源，占用资源的线程一直不放，别一方BLOCKED
        System.out.println("=====测试 BLOCKED==================");
        final Object resource = new Object();
        Thread threadBlockTestA = new Thread(()->{
            synchronized (resource){
                System.out.println("A抢到资源了");
                while(true);
            }
        });
        threadBlockTestA.setDaemon(Boolean.TRUE);//让threadBlockTestA 随着主线程退出，停掉while循环
        Thread threadBlockTestB = new Thread(()->{
            synchronized (resource){
                System.out.println("B抢到资源了");
            }
        });
        threadBlockTestB.setDaemon(Boolean.TRUE);//让threadBlockTestB 随着主线程退出而退出
        threadBlockTestA.start();//让A先抢资源
        Thread.sleep(10);
        threadBlockTestB.start();
        Thread.sleep(10);
        System.out.println(threadBlockTestA.getState());//A线程RUNNING
        System.out.println(threadBlockTestB.getState());//B线程BLOCKED

        System.out.println("=====测试 WAITING==================");
        Thread threadWaitingTest = new Thread(()->{
            synchronized (Thread.currentThread()){
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        });
        threadWaitingTest.start();
        Thread.sleep(1);
        System.out.println(threadWaitingTest.getState());
        threadWaitingTest.interrupt();

        System.out.println("=====测试 TIME_WAITING==================");
        Thread threadTimeWaitingTest = new Thread(()->{
            synchronized (Thread.currentThread()){
                try {
                    Thread.currentThread().wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadTimeWaitingTest.start();
        Thread.sleep(1);
        System.out.println(threadTimeWaitingTest.getState());//TIMED_WAITING
    }
    private static void testThreadPriority(){
        //获取操作系统的CPUs核数，使用Runtime
        Runtime.getRuntime().availableProcessors();
        //操作系统检测 正在等待线程，使用三种调度
        //Linux2.6使用O(1)调度，默认使用完全公平调度，Windows7/XP/VISTA/NT 使用多级反馈队列调度
        //轮询+优先，相同优先级，占用相同时间分片
        //并行与并发：并行相对微观（包含时间分片的并行），并发相对宏观：指两线程同时执行
        //优先级 跟平台调度器的实现有关，不能将线程优先级用来做标准
        Thread threadPriorityTest = new Thread();
        threadPriorityTest.setPriority(Thread.MAX_PRIORITY);//10
        threadPriorityTest.setPriority(Thread.NORM_PRIORITY);//5
        threadPriorityTest.setPriority(Thread.MIN_PRIORITY);//1
    }
    private static void testThreadIsDaemon(){
        //非守护进程依赖于守护进程，随着非守护进程的退出而退出
        //后端进程为非守护进程，并且不退出，就算主线程退出，程序也不会停止
        Thread threadDaemonTest = new Thread();
        System.out.println(String.format("默认创建的线程是否为守护线程：%b",threadDaemonTest.isDaemon()));//false
        threadDaemonTest.setDaemon(Boolean.TRUE);
    }

    private static  void testThreadInterrupted() throws InterruptedException {
        //object.interrupt() 设置当前线程中断标记为true
        //object.isInterrupted() 读取当前线程中断标记
        //Thread.interrupted() 如果清空当前线程中断标记，true->false
        //当碰到sleep/wait/join时，如果当前中断标记为true,会抛出InterruptedException+中断清除

        //7.1 未启动的线程，中断前后，中断标记都为false
        Thread threadInterruptTest1 = new Thread();
        System.out.println(threadInterruptTest1.isInterrupted());
        threadInterruptTest1.interrupt();
        System.out.println(threadInterruptTest1.isInterrupted());
        //7.2非sleep/wait/join的（循环用于模拟操作时长）例子中，中断标记为true时，不会中断程序
        Thread threadInterruptTest2 = new Thread(()->{
            int i=10;
            while(i-->0){//碰到中断标记，完整打印完10次
                System.out.println("当在循环中（非sleep/wait/join）"+ (11-i) +" 中断标记为："+Thread.currentThread().isInterrupted());
            }
        });
        threadInterruptTest2.start();
        threadInterruptTest2.interrupt();
        Thread.sleep(2000);
        //7.3 碰到wait + 中断标记为true --> 抛出异常,清除中断
        //在wait之前通过 Thread.interrupted() 清除中断标记，不会招聘异常，程序wait等待
        Thread threadInterruptTest3 = new Thread(()->{
            synchronized (Thread.currentThread()){
                try {
                    System.out.println("碰到wait之前的中断标记："+Thread.currentThread().isInterrupted());
                    Thread.currentThread().wait();//此处抛出异常,清除中断
                } catch (InterruptedException e) {
                    System.out.println("碰到wait之后的中断标记："+Thread.currentThread().isInterrupted()+ "error:"+e.getMessage());
                    //e.printStackTrace();//抛出异常
                }
            }
        });
        threadInterruptTest3.start();
        threadInterruptTest3.interrupt();

        Thread threadInterruptTest4 = new Thread(()->{
            synchronized (Thread.currentThread()){
                try {
                    Thread.interrupted();//执行手动清除中断标记
                    System.out.println("碰到wait之前清除标记，中断标记为："+Thread.currentThread().isInterrupted());
                    Thread.currentThread().wait(1000);//此处设置成时间wait等待超时，方便下面程序继续执行
                    System.out.println("碰到wait之后（已清除标记）等待超时后继续执行");
                } catch (InterruptedException e) {
                    e.printStackTrace();//这里不会被执行到
                }
            }
        });
        threadInterruptTest4.start();
        threadInterruptTest4.interrupt();
    }

    private static void testThreadJoin(){
        class A{
            private String result;
            public A(){
                result = "未完成";
            }
            public void setResult(String result){
                this.result = result;
            }
            public String getResult(){
                return result;
            }
        }
        A result = new A();//未完成
        Thread threadJoinTest = new Thread(()->{
            try {
                Thread.sleep(1000);
                result.setResult("完成了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadJoinTest.start();
        try {
            threadJoinTest.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(result.getResult());
    }

    private static void testThreadSleep(){
        Thread threadInterruptTest5 = new Thread(()->{
            synchronized (Thread.currentThread()){
                try {
                    System.out.println("碰到sleep之前的中断标记："+Thread.currentThread().isInterrupted());
                    Thread.sleep(1000);//此处抛出异常,清除中断
                } catch (InterruptedException e) {
                    System.out.println("碰到sleep之后的中断标记："+Thread.currentThread().isInterrupted()+ "error:"+e.getMessage());
                    e.printStackTrace();//抛出异常
                }
            }
        });
        threadInterruptTest5.start();
        threadInterruptTest5.interrupt();

        Thread threadInterruptTest6 = new Thread(()->{
            synchronized (Thread.currentThread()){
                try {
                    Thread.interrupted();//设置中断
                    System.out.println("碰到sleep之前清除标记，中断标记为："+Thread.currentThread().isInterrupted());
                    Thread.sleep(1000);//此处设置成时间sleep等待超时，方便下面程序执行完，主线程退出
                    System.out.println("碰到sleep之后（已清除标记）等待超时后继续执行");
                } catch (InterruptedException e) {
                    e.printStackTrace();//这里不会被执行到
                }
            }
        });
        threadInterruptTest6.start();
        threadInterruptTest6.interrupt();
    }
    public static void main(String ...args) throws InterruptedException {
        //1.创建线程
        testCreateThreadWays();

        //runnable1,runnable2用于下面例子
        Runnable runnable1 = () -> System.out.println("Hello,Runnable1");
        Runnable runnable2 = () -> System.out.println("Hello,Runnable2");//jdk8
        //2.线程的状态
        //线程名称
        testThreadName();

        //3.线程的存活状态，alive在启动前是false,启动后是true,离开run方法后是false
        testThreadIsAlive();
        //4.线程的执行状态 NEW/RUNNABLE/BLOCKED/WAITING/TIMED_WAITING/TERMINATED
        testThreadExecutionState();

        //5.线程的优先级
        testThreadPriority();

        //6.线程是否守护进程
        testThreadIsDaemon();

        //7.线程中断
        testThreadInterrupted();


        //8.线程合并,等待下载大文件，大量计算，耗时任务的完成，主线程处理结果
        testThreadJoin();

        //9.线程休眠
        testThreadSleep();

        Thread.sleep(2000);
        //10.线程的启动

        //说明：启动线程使用start而不是run方法，
        // start方法将run的逻辑委托给操作系统底层线程执行，
        // 直接使用run方法，相当于将run逻辑委托给当前主线程
        Thread startThread = new Thread(()->{
            System.out.println(Thread.currentThread().getName());
        },"XX");
        startThread.start(); //XX
        startThread.run(); //main

    }
}
