package javaapi.sun.misc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javaapi.java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Paths;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import sun.misc.MetaIndex;
import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;

public class Launcher {
    private static URLStreamHandlerFactory factory = new Launcher.Factory();
    private static Launcher launcher = new Launcher();
    private static String bootClassPath = System.getProperty("sun.boot.class.path");
    private ClassLoader loader;
    private static URLStreamHandler fileHandler;

    public static Launcher getLauncher() {
        return launcher;
    }

    public Launcher() {
        //创建系统 类加载器
        Launcher.ExtClassLoader var1;
        try {
            var1 = Launcher.ExtClassLoader.getExtClassLoader();
        } catch (IOException var10) {
            throw new InternalError("Could not create extension class loader", var10);
        }

        try {
            this.loader = Launcher.AppClassLoader.getAppClassLoader(var1);
        } catch (IOException var9) {
            throw new InternalError("Could not create application class loader", var9);
        }

        Thread.currentThread().setContextClassLoader(this.loader);
        //设置安全管理器
        String var2 = System.getProperty("java.security.manager");
        if (var2 != null) {
            SecurityManager var3 = null;
            if (!"".equals(var2) && !"default".equals(var2)) {
                try {
                    var3 = (SecurityManager)this.loader.loadClass(var2).newInstance();
                } catch (IllegalAccessException var5) {
                } catch (InstantiationException var6) {
                } catch (ClassNotFoundException var7) {
                } catch (ClassCastException var8) {
                }
            } else {
                var3 = new SecurityManager();
            }

            if (var3 == null) {
                throw new InternalError("Could not create SecurityManager: " + var2);
            }

            System.setSecurityManager(var3);//更新System中的安全管理器
        }

    }

    public ClassLoader getClassLoader() {
        return this.loader;
    }

    public static URLClassPath getBootstrapClassPath() {
        return Launcher.BootClassPathHolder.bcp;
    }

    //将文件转成EncodeURL,尾部加"/",前缀加"file:/"
    private static URL[] pathToURLs(File[] var0) {
        URL[] var1 = new URL[var0.length];

        for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = getFileURL(var0[var2]);
        }

        return var1;
    }
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\lib\resources.jar;
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\lib\rt.jar;
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\lib\sunrsasign.jar;
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\lib\jsse.jar;
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\lib\jce.jar;
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\lib\charsets.jar;
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\lib\jfr.jar;
    // D:\ProgramFiles\Java\jdk1.8.0_201\jre\classes
    private static File[] getClassPath(String var0) {
        File[] var1;
        if (var0 != null) {
            int var2 = 0;
            int var3 = 1;
            boolean var4 = false;

            int var5;//记录从哪个位置开始查找
            int var7;//记录File.pathSeparator 在var0中的索引
            //统计文件个数
            for(var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                ++var3;
            }

            var1 = new File[var3];
            var4 = false;
            //处理 a;b;c;
            for(var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                if (var7 - var5 > 0) {
                    var1[var2++] = new File(var0.substring(var5, var7));
                } else {
                    var1[var2++] = new File(".");//感觉多余
                }
            }
            //处理 a;b;c;d，最后的d
            if (var5 < var0.length()) {
                var1[var2++] = new File(var0.substring(var5));
            } else {
                var1[var2++] = new File(".");
            }

            if (var2 != var3) {//多余之后的补救，清除最后多余的new File(".");
                File[] var6 = new File[var2];
                System.arraycopy(var1, 0, var6, 0, var2);
                var1 = var6;
            }
        } else {
            var1 = new File[0];
        }

        return var1;
    }
    //将File转成URL,不是加上/作为开头，是目录尾部加上/结尾，protocol:file
    static URL getFileURL(File var0) {
        try {
            var0 = var0.getCanonicalFile();
        } catch (IOException var3) {
        }

        try {
            return ParseUtil.fileToEncodedURL(var0);
        } catch (MalformedURLException var2) {
            throw new InternalError(var2);
        }
    }
    //应用类加载器 定义，继承URLClassLoader
    static class AppClassLoader extends URLClassLoader {
        // 获取父类 URLClassLoader的ucp，由于ucp是私有的，通过SharedSecrets.getJavaNetAccess()方式
        // final URLClassPath ucp = SharedSecrets.getJavaNetAccess().getURLClassPath(this);
        final URLClassPath ucp = this.getURLClassPath();
        public static ClassLoader getAppClassLoader(final ClassLoader var0) throws IOException {
            final String var1 = System.getProperty("java.class.path");
            final File[] var2 = var1 == null ? new File[0] : Launcher.getClassPath(var1);
            return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<Launcher.AppClassLoader>() {
                public Launcher.AppClassLoader run() {
                    URL[] var1x = var1 == null ? new URL[0] : Launcher.pathToURLs(var2);
                    return new Launcher.AppClassLoader(var1x, var0);
                }
            });
        }

        AppClassLoader(URL[] var1, ClassLoader var2) {
            super(var1, var2, Launcher.factory);
            // 为了不报错,注释
            // this.ucp.initLookupCache(this);
        }

        public Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
            int var3 = var1.lastIndexOf(46);
            if (var3 != -1) {
                SecurityManager var4 = System.getSecurityManager();
                if (var4 != null) {
                    var4.checkPackageAccess(var1.substring(0, var3));
                }
            }
// 为了不报错，注释
/*            if (this.ucp.knownToNotExist(var1)) {
                Class var5 = this.findLoadedClass(var1);
                if (var5 != null) {
                    if (var2) {
                        this.resolveClass(var5);
                    }

                    return var5;
                } else {
                    throw new ClassNotFoundException(var1);
                }
            } else {
                return super.loadClass(var1, var2);
            }*/
return super.loadClass(var1, var2);
        }

        protected PermissionCollection getPermissions(CodeSource var1) {
            PermissionCollection var2 = super.getPermissions(var1);
            var2.add(new RuntimePermission("exitVM"));
            return var2;
        }

        private void appendToClassPathForInstrumentation(String var1) {
            assert Thread.holdsLock(this);

            super.addURL(Launcher.getFileURL(new File(var1)));
        }

        private static AccessControlContext getContext(File[] var0) throws MalformedURLException {
            PathPermissions var1 = new PathPermissions(var0);
            ProtectionDomain var2 = new ProtectionDomain(new CodeSource(var1.getCodeBase(), (Certificate[])null), var1);
            AccessControlContext var3 = new AccessControlContext(new ProtectionDomain[]{var2});
            return var3;
        }

        static {
            ClassLoader.registerAsParallelCapable();
        }
    }
    //启动类路径 持有者 类定义
    private static class BootClassPathHolder {
        static final URLClassPath bcp;

        private BootClassPathHolder() {
        }

        static {
            URL[] var0;
            if (Launcher.bootClassPath != null) {
                var0 = (URL[])AccessController.doPrivileged(new PrivilegedAction<URL[]>() {
                    public URL[] run() {
                        //读取系统变量： sun.boot.class.path，获取类路径下文件集合
                        File[] var1 = Launcher.getClassPath(Launcher.bootClassPath);
                        int var2 = var1.length;
                        HashSet var3 = new HashSet();//存放结果

                        for(int var4 = 0; var4 < var2; ++var4) {
                            File var5 = var1[var4];
                            if (!var5.isDirectory()) {//非目录，转成目录
                                var5 = var5.getParentFile();
                            }

                            if (var5 != null && var3.add(var5)) {//添加目录（不重复）成功
                                MetaIndex.registerDirectory(var5);//读取meta-index文件，缓存到MetaIndex.jarMap
                            }
                        }

                        return Launcher.pathToURLs(var1);//将sun.boot.class.path目录下所有文件转成EncodedURL
                    }
                });
            } else {
                var0 = new URL[0];
            }
            //创建URLClassPath实例（收集得到的URL，URLStreamHandler工厂，acc=null）
            bcp = new URLClassPath(var0, Launcher.factory, (AccessControlContext)null);
            // 为了不报错,注释
            // bcp.initLookupCache((ClassLoader)null);//不传类加载器，即不缓存,原因这是初始化BOOT启动类库
        }
    }
    //扩展类加载器 定义，继承URLClassLoader
    static class ExtClassLoader extends URLClassLoader {
        private static volatile Launcher.ExtClassLoader instance;

        public static Launcher.ExtClassLoader getExtClassLoader() throws IOException {
            if (instance == null) {
                Class var0 = Launcher.ExtClassLoader.class;
                synchronized(Launcher.ExtClassLoader.class) {
                    if (instance == null) {
                        instance = createExtClassLoader();
                    }
                }
            }

            return instance;
        }

        private static Launcher.ExtClassLoader createExtClassLoader() throws IOException {
            try {
                return (Launcher.ExtClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<Launcher.ExtClassLoader>() {
                    public Launcher.ExtClassLoader run() throws IOException {
                        File[] var1 = Launcher.ExtClassLoader.getExtDirs();
                        int var2 = var1.length;

                        for(int var3 = 0; var3 < var2; ++var3) {
                            MetaIndex.registerDirectory(var1[var3]);
                        }

                        return new Launcher.ExtClassLoader(var1);
                    }
                });
            } catch (PrivilegedActionException var1) {
                throw (IOException)var1.getException();
            }
        }

        void addExtURL(URL var1) {
            super.addURL(var1);
        }

        public ExtClassLoader(File[] var1) throws IOException {
            super(getExtURLs(var1), (ClassLoader)null, Launcher.factory);
            // 为了不报错，注释
            // SharedSecrets.getJavaNetAccess().getURLClassPath(this).initLookupCache(this);
        }

        private static File[] getExtDirs() {
            String var0 = System.getProperty("java.ext.dirs");
            File[] var1;
            if (var0 != null) {
                StringTokenizer var2 = new StringTokenizer(var0, File.pathSeparator);
                int var3 = var2.countTokens();
                var1 = new File[var3];

                for(int var4 = 0; var4 < var3; ++var4) {
                    var1[var4] = new File(var2.nextToken());
                }
            } else {
                var1 = new File[0];
            }

            return var1;
        }

        private static URL[] getExtURLs(File[] var0) throws IOException {
            Vector var1 = new Vector();

            for(int var2 = 0; var2 < var0.length; ++var2) {
                String[] var3 = var0[var2].list();
                if (var3 != null) {
                    for(int var4 = 0; var4 < var3.length; ++var4) {
                        if (!var3[var4].equals("meta-index")) {
                            File var5 = new File(var0[var2], var3[var4]);
                            var1.add(Launcher.getFileURL(var5));
                        }
                    }
                }
            }

            URL[] var6 = new URL[var1.size()];
            var1.copyInto(var6);
            return var6;
        }

        public String findLibrary(String var1) {
            var1 = System.mapLibraryName(var1);
            URL[] var2 = super.getURLs();
            File var3 = null;

            for(int var4 = 0; var4 < var2.length; ++var4) {
                URI var5;
                try {
                    var5 = var2[var4].toURI();
                } catch (URISyntaxException var9) {
                    continue;
                }

                File var6 = Paths.get(var5).toFile().getParentFile();
                if (var6 != null && !var6.equals(var3)) {
                    String var7 = VM.getSavedProperty("os.arch");
                    File var8;
                    if (var7 != null) {
                        var8 = new File(new File(var6, var7), var1);
                        if (var8.exists()) {
                            return var8.getAbsolutePath();
                        }
                    }

                    var8 = new File(var6, var1);
                    if (var8.exists()) {
                        return var8.getAbsolutePath();
                    }
                }

                var3 = var6;
            }

            return null;
        }

        private static AccessControlContext getContext(File[] var0) throws IOException {
            PathPermissions var1 = new PathPermissions(var0);
            ProtectionDomain var2 = new ProtectionDomain(new CodeSource(var1.getCodeBase(), (Certificate[])null), var1);
            AccessControlContext var3 = new AccessControlContext(new ProtectionDomain[]{var2});
            return var3;
        }

        static {
            ClassLoader.registerAsParallelCapable();
            instance = null;
        }
    }
    //实现java.net.URLStreamHandlerFactory，反射创建URL流处理器（sun.net.www.protocol.XXX.Handler）
    private static class Factory implements URLStreamHandlerFactory {
        private static String PREFIX = "sun.net.www.protocol";

        private Factory() {
        }

        public URLStreamHandler createURLStreamHandler(String var1) {
            String var2 = PREFIX + "." + var1 + ".Handler";

            try {
                Class var3 = Class.forName(var2);
                return (URLStreamHandler)var3.newInstance();
            } catch (ReflectiveOperationException var4) {
                throw new InternalError("could not load " + var1 + "system protocol handler", var4);
            }
        }
    }
}
