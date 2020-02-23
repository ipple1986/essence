import java.util.Random;

public class Chapter2 {
//同步Synchronization
    //多线程的问题：竞争条件，数据竞争，缓存变量
    //竞争条件：当一个计算的正确性依赖于调度程序的相对定时或多线程交叉时，会发生竞争条件
    /*
    if (a == 10.0)
        b = a / 2.0;
    eg.
    a,b在单线程或多线程的局部变量，线程安全
    a,b为两个实例或类的静态变量时，多线程会有安全问题
        **当线程A执行完判断后，被调度程序给挂起，此时B线程将a做修改，那么当A线程恢复后，b的值就不等于5，原因a被B线程修改过
    1.check-then-act:用过去的观察数据，来决定下一步
    2.read-modify-write:三步不可分隔，如下例，counter=1
        public int getID(){
            return counter++;
        }
    //数据竞争（多线程访问同一内存，顺序/行为不确定性），至少两个以上线程竞争同一内存地址，至少一个是写数据的线程，线程彼此间无协同访问，因而访问顺序是不确定的
        private static Parser parser;
        public static Parser getInstance(){
            if (parser == null)
                parser = new Parser();
            return parser;
        }
        线程1第一次发现null就实例Parser,线程2有可能发现parser为null,也有可能发现parser不为null，
        no happen-before ordering 无协同访问顺序

     //缓存的变量
     性能提升，编译器，jvm，操作系统合作将变量 缓存 到寄存器或CPU级别缓存中，而不是依赖主内存。
     每个线程保存自己的一份变量复本，写数据只写到自己复本中，其他线程不可能看得到对这些数据的更新
    private static BigDecimal result; //result是个缓存 变量，
    public static void main(String[] args){
            Runnable r = () ->{
                result = computePi(50000); //worker线程有它自己的复本
            };
            Thread t = new Thread(r);
            t.start();
            try{
                t.join();
            }catch (InterruptedException ie){
                ......
            }
            System.out.println(result);//此处主线程有自己的复本
    }
     */
    private static void checkCurrentThreadHoldsGivenObjectLock(){
        thisReference = new Chapter2();
        //测试同步块
        thisReference.testSynchronizedBlock();
        //测试静态同步方法
        testStaticSynchronizedMethod();
        //测试非静态方法
        thisReference.testNontestStaticSynchronizedMethod();
    }
    private void testSynchronizedBlock(){
        //字节类 对象锁
        System.out.println("--测试-同步块-----字节类 对象锁------------------");
        Class<Chapter2> clz = Chapter2.class;
        System.out.println(String.format("【字节类对象锁】【sychronized块之前】当前线程是否获得字节对象锁：%b",Thread.holdsLock(clz)));//false
        synchronized (clz){
            System.out.println(String.format("【字节类对象锁】【sychronized块内】当前线程是否获得字节对象锁：%b",Thread.holdsLock(clz)));//true
        }
        System.out.println(String.format("【字节类对象锁】【sychronized块之后】当前线程是否获得字节对象锁：%b",Thread.holdsLock(clz)));//false
        System.out.println("--测试-同步块-----Object实例 对象锁------------------");
        //Object实例对象锁
        Object o = new Object();
        System.out.println(String.format("【当前线程对象锁】【sychronized块之前】当前线程是否获得字节对象锁：%b",Thread.holdsLock(0)));//false
        synchronized (o){
            System.out.println(String.format("【当前线程对象锁】【sychronized块内】当前线程是否获得字节对象锁：%b",Thread.holdsLock(o)));//true
        }
        System.out.println(String.format("【当前线程对象锁】【sychronized块之后】当前线程是否获得字节对象锁：%b",Thread.holdsLock(o)));//false

        System.out.println("--测试-同步块----当前线程实例 对象锁-------------------");
        //当前线程实例 对象锁
        Thread currentThread =Thread.currentThread();
        System.out.println(String.format("【当前线程对象锁】【sychronized块之前】当前线程是否获得字节对象锁：%b",Thread.holdsLock(currentThread)));//false
        synchronized (currentThread){
            System.out.println(String.format("【当前线程对象锁】【sychronized块内】当前线程是否获得字节对象锁：%b",Thread.holdsLock(currentThread)));//true
        }
        System.out.println(String.format("【当前线程对象锁】【sychronized块之后】当前线程是否获得字节对象锁：%b",Thread.holdsLock(currentThread)));//false

    }
    static class B{}
    private static synchronized void testStaticSynchronizedMethod(){
        System.out.println("--测试-----静态同步方法 字节类对象锁-------------------");
        //true
        System.out.println(String.format("【静态同步方法，】,是否获取Chapter2.class的对象锁：%b",Thread.holdsLock(Chapter2.class)));
        class A{}
        //false false
        System.out.println(String.format("【静态同步方法，】,是否获取A.class,B.class的对象锁：%b %b",Thread.holdsLock(A.class),Thread.holdsLock(B.class)));
    }
    private  static Chapter2 thisReference = null;
    private synchronized void testNontestStaticSynchronizedMethod(){
        System.out.println("--测试-----非静态同步方法 字节类对象锁-------------------");
        //false
        System.out.println(String.format("【非静态同步方法，】,是否获取Chapter2.class的对象锁：%b",Thread.holdsLock(Chapter2.class)));
        class A{}
        //false false
        System.out.println(String.format("【非静态同步方法，】,是否获取A.class,B.class的对象锁：%b %b",Thread.holdsLock(A.class),Thread.holdsLock(B.class)));
        //true true
        System.out.println(String.format("【非静态同步方法，】,是否获取this,thisReference的对象锁：%b %b",Thread.holdsLock(this),Thread.holdsLock(thisReference)));
    }
//================================================================================================================
    double a = 10.0,b;

    private static void  testReadThenActRaceCondition(){
        Runnable modifyRunnalbe = new ReadThenActRaceConditionTask.ModifyARunnable();
        Runnable readThenActRunnalbe = new ReadThenActRaceConditionTask.ReadThenActRunnable();
        int i= 10000;
        while(i-->0){
            new Thread(readThenActRunnalbe).start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i=5000;
        while(i-->0){
            new Thread(modifyRunnalbe).start();
        }


    }
    static class ReadThenActRaceConditionTask{
        static double a = 10.0,b=0;
        static  Random random = new Random();
        static class ModifyARunnable implements Runnable{
            @Override
            public void run() {
                int i = random.nextInt(10);
                a = 15;
                //System.out.println(a=15);
                //System.out.println(b);
            }
        }
        static class ReadThenActRunnable implements Runnable{
            @Override
            public void run() {
                if(a==10.0)
                    b = a/5.0;
                if(b!=0 && b!=2 )System.out.println(b);
                //理想需要b=0/2，有可能是4
            }
        }
    }
    public static void main(String...args){
        //synchronized 同步锁/排它锁，关注区域进行序列化，只有一个线程获得对象锁，并且进入该区域；每个Java对象都是对象锁
        //内存可见性，从 主内存中读数据到该区域，将该区域数据写回主内存

        //1.判断当前线程是否获得锁
        /*
        Thread.holdsLock(Object o)
         */

        //checkCurrentThreadHoldsGivenObjectLock();
        testReadThenActRaceCondition();
        //2.同步方法，如下例，解决read-modify-write 竞争条件
        /*
        public [static] synchronized int getID(){//此处加synchronized
            return counter++;
        }
        */

        //3.同步块(对象锁可以不是该字节类，或该类实例对象，可以是任意对象),解决 缓存的变量问题
        /*
        synchronized(Lock){
            .....
        }
        解决缓存的变量result,使用两个synchronized块，一把相同的锁FOUR
        Runnable r = () -> {
            synchronized(FOUR){
                result = computePi(50000);
            }
        };
        // …
        synchronized(FOUR){
            System.out.println(result);
        }
        //4.意识到活跃性问题：程序无法进一步往下执行
            ** 单线程无限循环
            ** 多线程的死锁活锁饥饿
            死锁：Ａ持有Ｂ需要的锁，Ｂ持有Ａ需要的锁
            活锁：线程不断地重试一个失败的操作，但一直失败，导致无法往下执行别的
            饥饿：优先级高的被调度器优先处理，低级别的线程无限延期等待被执行（有可能又出现高优先级的线程被调度，导致自己往后再排队）
        //5.Volatile变量 + Final变量
         Volatile/Final变量 只保持内存可见性，不具备同步，只读写主内存，不读本地复本
         **注意在32位机器上声明Volatile的long,double变量存在线程安全，原因long,double被拆成两次操作
            局部变量不能声明Volatile变量，Final变量也不能声明成Volatile变量
            在final类中定义不可变final属性
                *不可变类不允许状态被修改
                * 所有属性都定义成final
                * 类中，this引用只能出现在构造器中，不能逃出构造器，下面例子是反例
                public class ThisEscapeDemo{
                    private static ThisEscapeDemo lastCreatedInstance;
                    public ThisEscapeDemo(){
                        lastCreatedInstance = this;
                    }
                }


        */
    }
}
