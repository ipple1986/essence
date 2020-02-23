//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package javaapi.sun.misc;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.Properties;
//import sun.misc.Launcher.ExtClassLoader;
import sun.misc.OSEnvironment;
import sun.misc.VMNotification;

public class VM {
    private static boolean suspended = false;
    /** @deprecated */
    @Deprecated
    public static final int STATE_GREEN = 1;
    /** @deprecated */
    @Deprecated
    public static final int STATE_YELLOW = 2;
    /** @deprecated */
    @Deprecated
    public static final int STATE_RED = 3;

    private static volatile boolean booted = false;
    private static final Object lock = new Object();
    //64G 默认直接内存的大小
    private static long directMemory = 67108864L;
    //是否支持：页对齐直接内存
    private static boolean pageAlignDirectMemory;
    //是否允许数组语法
    private static boolean defaultAllowArraySyntax = false;
    private static boolean allowArraySyntax;
    private static final Properties savedProps;
    //final引用计数
    private static volatile int finalRefCount;
    private static volatile int peakFinalRefCount;//上限final引用计数

    private static final int JVMTI_THREAD_STATE_ALIVE = 1;//00000001
    private static final int JVMTI_THREAD_STATE_TERMINATED = 2;//00000010
    private static final int JVMTI_THREAD_STATE_RUNNABLE = 4;//00000100
    private static final int JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER = 1024;//10000000000
    private static final int JVMTI_THREAD_STATE_WAITING_INDEFINITELY = 16;//00001000
    private static final int JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT = 32;//00010000

    public VM() {
    }

    /** @deprecated */
    @Deprecated
    public static boolean threadsSuspended() {
        return suspended;
    }

    public static boolean allowThreadSuspension(ThreadGroup var0, boolean var1) {
        return var0.allowThreadSuspension(var1);
    }

    /** @deprecated */
    @Deprecated
    public static boolean suspendThreads() {
        suspended = true;
        return true;
    }

    /** @deprecated */
    @Deprecated
    public static void unsuspendThreads() {
        suspended = false;
    }

    /** @deprecated */
    @Deprecated
    public static void unsuspendSomeThreads() {
    }

    /** @deprecated */
    @Deprecated
    public static final int getState() {
        return 1;
    }

    /** @deprecated */
    @Deprecated
    public static void registerVMNotification(VMNotification var0) {
    }

    /** @deprecated */
    @Deprecated
    public static void asChange(int var0, int var1) {
    }

    /** @deprecated */
    @Deprecated
    public static void asChange_otherthread(int var0, int var1) {
    }

    public static void booted() {
        synchronized(lock) {
            booted = true;
            lock.notifyAll();
        }
    }

    public static boolean isBooted() {
        return booted;
    }

    //VM未启动，wait等待
    public static void awaitBooted() throws InterruptedException {
        synchronized(lock) {
            while(!booted) {
                lock.wait();
            }
        }
    }

    public static long maxDirectMemory() {
        return directMemory;
    }

    public static boolean isDirectMemoryPageAligned() {
        return pageAlignDirectMemory;
    }

    public static boolean allowArraySyntax() {
        return allowArraySyntax;
    }
    //是否系统级域加载器
    public static boolean isSystemDomainLoader(ClassLoader var0) {
        return var0 == null;
    }
    //从savedProps 获取属性值
    public static String getSavedProperty(String var0) {
        if (savedProps.isEmpty()) {
            throw new IllegalStateException("Should be non-empty if initialized");
        } else {
            return savedProps.getProperty(var0);
        }
    }
    //VM未启动时，初始化savedProps，并修改直接内存directMemory大小，是否直接内存分页对齐，数组语法
    public static void saveAndRemoveProperties(Properties var0) {
        if (booted) {
            throw new IllegalStateException("System initialization has completed");
        } else {
            savedProps.putAll(var0);
            String var1 = (String)var0.remove("sun.nio.MaxDirectMemorySize");
            if (var1 != null) {
                if (var1.equals("-1")) {//未设置最大直接内存，刚使用运行时可用最大内存
                    directMemory = Runtime.getRuntime().maxMemory();
                } else {
                    long var2 = Long.parseLong(var1);
                    if (var2 > -1L) {
                        directMemory = var2;//读取配置，设置直接内存大小
                    }
                }
            }
            //是否支持 分页对齐直接内存
            var1 = (String)var0.remove("sun.nio.PageAlignDirectMemory");
            if ("true".equals(var1)) {
                pageAlignDirectMemory = true;
            }
            //是否允许数组语法
            var1 = var0.getProperty("sun.lang.ClassLoader.allowArraySyntax");
            allowArraySyntax = var1 == null ? defaultAllowArraySyntax : Boolean.parseBoolean(var1);
            //移除不必要配置
            var0.remove("java.lang.Integer.IntegerCache.high");
            var0.remove("sun.zip.disableMemoryMapping");
            var0.remove("sun.java.launcher.diag");
            var0.remove("sun.cds.enableSharedLookupCache");
        }
    }
    //未启动VM前，初始化操作系统环境
    //读取系统变量 sun.io.allowCriticalErrorMessageBox ，设置错误模式ERROR MODE
    //每个版本JDK有自己的实现类
    public static void initializeOSEnvironment() {
        if (!booted) {
            OSEnvironment.initialize();
        }
    }

    public static int getFinalRefCount() {
        return finalRefCount;
    }

    public static int getPeakFinalRefCount() {
        return peakFinalRefCount;
    }

    public static void addFinalRefCount(int var0) {
        finalRefCount += var0;
        if (finalRefCount > peakFinalRefCount) {
            peakFinalRefCount = finalRefCount;
        }

    }
    //int转 Thread.State
    public static State toThreadState(int var0) {
        if ((var0 & 4) != 0) {
            return State.RUNNABLE;
        } else if ((var0 & 1024) != 0) {
            return State.BLOCKED;
        } else if ((var0 & 16) != 0) {
            return State.WAITING;
        } else if ((var0 & 32) != 0) {
            return State.TIMED_WAITING;
        } else if ((var0 & 2) != 0) {
            return State.TERMINATED;
        } else {
            return (var0 & 1) == 0 ? State.NEW : State.RUNNABLE;
        }
    }
    //获取最后用户定义的类加载器
    public static native ClassLoader latestUserDefinedLoader0();

    //获取用户类加载器，如果没找到就取ExtClassLoader扩展类加载器
    public static ClassLoader latestUserDefinedLoader() {
        ClassLoader var0 = latestUserDefinedLoader0();
        if (var0 != null) {
            return var0;
        } else {
            try {
                // 此处为了不让报错，引入javaapi.下的包
                return javaapi.sun.misc.Launcher.ExtClassLoader.getExtClassLoader();
            } catch (IOException var2) {
                return null;
            }
        }
    }

    private static native void initialize();

    static {
        allowArraySyntax = defaultAllowArraySyntax;
        savedProps = new Properties();
        finalRefCount = 0;
        peakFinalRefCount = 0;
        initialize();//调用JVM 底层初始化
    }
}
