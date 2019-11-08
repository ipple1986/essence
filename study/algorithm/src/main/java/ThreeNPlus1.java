import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Matcher;

public class ThreeNPlus1 {
    static Random random = new Random();
    private static Integer[] mock(Integer N){
        if(N==null)N = random.nextInt(100)+1;
        Integer diffNum = random.nextInt(10)+1;
        Integer diffNumIdx = random.nextInt(3*N+1);
        Integer sameNum = random.nextInt(10)+10;
        Integer[] A = new Integer[3*N+1];
        for(int i=0;i<3*N +1;i++){
            if(diffNumIdx.equals(i)){
                A[i] = diffNum;
            }else{
                A[i] = sameNum;
            }
        }
        return A;
    }
    public static void  main(String ... args){
/*
        // 随机生成3N+1数组
        Integer[] A = mock(null);
        System.out.println(Arrays.asList(A));
        // 递归查找不重复数
        Integer result = findDiff(0,A);
        System.out.println(result);*/

//        testImagCode();
        //for(Object prop : System.getProperties().keySet())
/*        System.out.println(System.getProperties().keySet());
        System.out.println(System.getenv().keySet());

        System.out.println(Arrays.asList(TimeZone.getAvailableIDs()));
        System.out.println(Arrays.asList(Locale.getAvailableLocales()));

        System.out.println(new GregorianCalendar().getTime());
        System.out.println(TimeZone.getDefault() +"\t" + TimeZone.getTimeZone("Africa/Abidjan"));

        System.out.println("修改时区前(默认)：" + Calendar.getInstance().getTime());
        System.out.println("修改时区前(非洲科特迪瓦 阿比让市)：" + Calendar.getInstance(TimeZone.getTimeZone("Africa/Abidjan")).getTime());
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Abidjan"));
        System.out.println("修改时区后：" +Calendar.getInstance(TimeZone.getTimeZone("Africa/Abidjan")).getTime());
        TimeZone.setDefault(null);
        System.out.println("恢复时区后：" +Calendar.getInstance(TimeZone.getTimeZone("Africa/Abidjan")).getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Africa/Abidjan"));
        System.out.println("恢复时区后：" +calendar.getTime());*/

        // 总结：
        // java中数组最长是 Integer.MAX_VALUE-2
        // 创建Object数组分配内存过多发生OOM
        // 线程创建太多导致OOM
        // 递归调用无终止条件发生SOE

        // OOM
        // java.lang.OutOfMemoryError: Requested array size exceeds VM limit
        int  size = Integer.MAX_VALUE;
        size = Integer.MAX_VALUE-1;
        //byte[] bytes = new byte[size];
        // java.lang.OutOfMemoryError: Java heap space
        size = Integer.MAX_VALUE-2;
        //Object[] integers = new Object[size];


        // java.lang.OutOfMemoryError: unable to create new native thread
        // 原因：堆外内存不足，或系统限制创建线程数
        // 递归创建线程数 window,8G内存，jdk8  20万+个线程（window无限线程创建数目，内存8G不足，导致程序僵死）
        final AtomicLong i= new AtomicLong(0);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    new Thread(this, "subThread" + i.incrementAndGet()).start();
                    System.out.println("当前线程："+i.get());
                }catch (Error e ){
                    System.out.println("最大线程数：" + i.decrementAndGet());
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2*60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //new Thread(runnable,"Main").start();

        // 并行执行创建线程，线程数达到20万左右，出现僵死状况
        /*Runnable runnable2 = new Runnable() {
            public void run() {
                System.out.println("当前线程数：" + i.incrementAndGet());
                synchronized (this){
                    try {
                        wait(2*60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        for(int ii=0;ii<Integer.MAX_VALUE;ii++){
              new Thread(runnable2).start();
        }*/


        // SOE
        //java.lang.StackOverflowError
        class Recurisiable{
            public  void recurisiveMethod(int level){
                System.out.println(level++);//10456 1万多层
                    /*for(int ii=0;ii<1000;ii++){ // 2万多层
                        Object o = new Object();
                        Object o1 = new Object();
                    }*/
                recurisiveMethod(level);
            }
        }
        //new Recurisiable().recurisiveMethod(1);
        // 死锁不会 使CPU占用过高
/*        final Object lock1 = new Object();
        final Object lock2 = new Object();
        new Thread(new Runnable() {
                public void run() {
                    synchronized (lock1) {
                        for (int k = 0; k < 100000; k++) {
                            //System.out.println(1);
                        }
                        synchronized (lock2) {
                        }
                    }
                }
        }, "死锁线程1").start();

        new Thread(new Runnable() {
                public void run() {
                    synchronized (lock2) {
                        for (int k = 0; k < 100000; k++) {}
                        synchronized (lock1) {}
                    }
                }
            }, "死锁线程2").start();*/

        // 读大文件
        

        // 死循环
        BigDecimal bigDecimal = new BigDecimal(Long.MAX_VALUE);
        BigDecimal bigDecimal2 = new BigDecimal(Long.MAX_VALUE);
        // 内存过高，导致全GC
        // 模拟堆内存泄露时，CPU飙升
        for(int threadNum = 0;threadNum<100000;threadNum++){
            new Thread(new Runnable() {
                public void run() {
                    Object[] objects = new Object[Integer.MAX_VALUE/2];
                }
            }).start();
        }
        //LockSupport.park();

/*        new Thread(new Runnable() {
            public void run() {
                for(int i=0;i<10000000l;i++)
                    System.out.println("=======================================");
            }
        }).start();*/
/*        final Map<String,String> map = new HashMap<String, String>();
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new Runnable() {
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                map.put("A","BB");
            }
        },"Thread1").start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                map.put("A","AA");
            }
        },"Thread2").start();
        countDownLatch.countDown();
        countDownLatch.countDown();

        for(String key:map.keySet()){
            System.out.println(map.get("A"));
        }*/
    }

    private static Integer findDiff(int loop,Integer ... A){
        int N = (A.length-1)/3; // 计算出N
        if(loop < N ){
            if( A[loop] + A[loop+N] == 2* A[loop+2*N] ){ // 三数重复
                return findDiff(loop+1,A);
            }else{ // 存在不重复数
                return  A[loop] == A[loop+N] ? A[loop+2*N]:( A[loop] == A[loop+2*N] ? A[loop+N]:A[loop]);
            }
        }else{
            return A[A.length-1];
        }
    }
    // https://mp.weixin.qq.com/cgi-bin/verifycode?r=1567302287212
    private static void testImagCode(){
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new MemoryCacheImageInputStream(ThreeNPlus1.class.getResourceAsStream("verifycode.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int white  = Color.white.getRGB();
        int black  = Color.black.getRGB();

        BufferedImage bi2= new BufferedImage(bi.getWidth(), bi.getHeight(),BufferedImage.TYPE_BYTE_BINARY);;
        for (int i = 0; i < bi.getWidth(); i++) {
            for (int j = 0; j < bi.getHeight(); j++) {
                int origin  = bi.getRGB(i, j);
                float avg = ((origin & 0x00ff0000) >>> 16 + (origin & 0x0000ff00) >>> 8 + (origin & 0x000000ff));
                System.out.println(avg);
                if(avg > 0 )bi2.setRGB(i,j,black);
                else bi2.setRGB(i,j,white);
                //System.out.println(i+","+j+"  from:"+srcColor.getRGB()+"to"+targetColor.getRGB());
                //bi.setRGB(i, j, targetColor.getRGB());
            }
        }


        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = it.next();
        String filePath  = ThreeNPlus1.class.getResource("").getPath().concat("/v.jpg");
        File f = new File(filePath);
        System.out.println(filePath);
        ImageOutputStream ios = null;
        try {
            ios = ImageIO.createImageOutputStream(f);
            writer.setOutput(ios);
            writer.write(bi2);
            bi2.flush();
            ios.flush();
            ios.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        }

    }
}
