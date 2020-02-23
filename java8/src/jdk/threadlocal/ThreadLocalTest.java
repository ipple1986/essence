package jdk.threadlocal;

import java.security.AccessController;
import java.security.SecurityPermission;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalTest {
//ConcurrentHashMap

    // test thread's threadLocals equals null
    public static void testNullThreadLocalMap(){

        new Thread(()-> {
            System.out.println(Thread.currentThread());
        }).start();

        new Thread(()-> {
            ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
            System.out.println(Thread.currentThread()+ threadLocal.toString());
        }).start();

        new Thread(()-> {
            ThreadLocal<String> threadLocal = new ThreadLocal<>();
             System.out.println(Thread.currentThread()+ threadLocal.toString());
        }).start();
    }
    public static void main (String ... arg){
       //
        //AccessController.checkPermission(new SecurityPermission);
        //System.out.println(securityManager);
        AccessController.doPrivileged(new java.security.PrivilegedAction<Void>(){
            public Void run(){
                SecurityManager securityManager = System.getSecurityManager();
                System.out.println(securityManager);
                //testNullThreadLocalMap();
                return null;
            }
        });
    }
}
