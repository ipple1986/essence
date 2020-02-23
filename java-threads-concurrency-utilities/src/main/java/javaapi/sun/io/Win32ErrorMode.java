package javaapi.sun.io;

import sun.misc.VM;

public class Win32ErrorMode {
    private static final long SEM_FAILCRITICALERRORS = 1L;//
    private static final long SEM_NOGPFAULTERRORBOX = 2L;
    private static final long SEM_NOALIGNMENTFAULTEXCEPT = 4L;
    private static final long SEM_NOOPENFILEERRORBOX = 32768L;

    private Win32ErrorMode() {
    }
    //VM未启动时，设置系统，不允许关键错误消息框
    public static void initialize() {
        if (!VM.isBooted()) {
            String var0 = System.getProperty("sun.io.allowCriticalErrorMessageBox");//我的win10,null
            if (var0 == null || var0.equals(Boolean.FALSE.toString())) {
                long var1 = setErrorMode(0L);
                var1 |= 1L;//最底位二进制设置为1
                setErrorMode(var1);
            }
        }

    }

    private static native long setErrorMode(long var0);
}
