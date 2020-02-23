package javaapi.java.net;

import java.io.Closeable;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.InetAddress;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.misc.Resource;
import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;
import sun.security.util.SecurityConstants;


// 1.2
public class URLClassLoader extends SecureClassLoader implements Closeable {
    // 字节类或资源的查找路径
    private final URLClassPath ucp;

    // 为让子类不报错，自己加的
    protected  URLClassPath getURLClassPath(){
        return this.ucp;
    }
    // 加载字节类或资源的访问上下文
    private final AccessControlContext acc;

    // 构造器
    public URLClassLoader(URL[] urls, ClassLoader parent) {
        super(parent);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.acc = AccessController.getContext();
        ucp = new URLClassPath(urls, acc);
    }

    URLClassLoader(URL[] urls, ClassLoader parent,
                   AccessControlContext acc) {
        super(parent);
        // this is to make the stack depth consistent with 1.1
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.acc = acc;
        ucp = new URLClassPath(urls, acc);
    }

    public URLClassLoader(URL[] urls) {
        super();
        // this is to make the stack depth consistent with 1.1
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.acc = AccessController.getContext();
        ucp = new URLClassPath(urls, acc);
    }

    URLClassLoader(URL[] urls, AccessControlContext acc) {
        super();
        // this is to make the stack depth consistent with 1.1
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.acc = acc;
        ucp = new URLClassPath(urls, acc);
    }
    // URLStreamHandlerFactory JarFile的JarHandler
    public URLClassLoader(URL[] urls, ClassLoader parent,
                          URLStreamHandlerFactory factory) {
        super(parent);
        // this is to make the stack depth consistent with 1.1
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        acc = AccessController.getContext();
        ucp = new URLClassPath(urls, factory, acc);
    }

    // 用于跟踪可关闭的jar文件或文件输入流，http资源我们不需要关注，因为不需要关闭
    // 通过URLClassLoader.close()关闭jar文件
    // 每个jar文件缓存一次
    private WeakHashMap<Closeable,Void>
        closeables = new WeakHashMap<>();

    // 递归从父类加载器加载资源，找不到调findResource
    // 找到资源就缓存 并 打开（针对jar）
    public InputStream getResourceAsStream(String name) {
        URL url = getResource(name);
        try {
            if (url == null) {
                return null;
            }
            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream();
            if (urlc instanceof JarURLConnection) {
                JarURLConnection juc = (JarURLConnection)urlc;
                JarFile jar = juc.getJarFile();
                synchronized (closeables) {
                    if (!closeables.containsKey(jar)) {//缓存
                        closeables.put(jar, null);
                    }
                }
            } else if (urlc instanceof sun.net.www.protocol.file.FileURLConnection) {
                synchronized (closeables) {
                    closeables.put(is, null);
                }
            }
            return is;
        } catch (IOException e) {
            return null;
        }
    }

   // 关闭所有URLClassPath.Loader 打开的流
    public void close() throws IOException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(new RuntimePermission("closeClassLoader"));
        }
        // 关闭 URLClassPath中的loaders,返回异常集合
        List<IOException> errors = ucp.closeLoaders();

        // 关闭弱引用可关闭流
        synchronized (closeables) {
            Set<Closeable> keys = closeables.keySet();
            for (Closeable c : keys) {
                try {
                    c.close();
                } catch (IOException ioex) {
                    errors.add(ioex);
                }
            }
            closeables.clear();
        }

        if (errors.isEmpty()) {
            return;
        }
        //拼接异常链，并抛出
        IOException firstex = errors.remove(0);
        for (IOException error: errors) {
            firstex.addSuppressed(error);
        }
        throw firstex;
    }

    // 添加URL
    protected void addURL(URL url) {
        ucp.addURL(url);
    }

    // 查询所有的URLs
    public URL[] getURLs() {
        return ucp.getURLs();
    }

    // 覆盖父类ClassLoader的空实现,用于递归找不到类时，使用
    protected Class<?> findClass(final String name)
        throws ClassNotFoundException
    {
        final Class<?> result;
        try {
            result = AccessController.doPrivileged(
                new PrivilegedExceptionAction<Class<?>>() {
                    public Class<?> run() throws ClassNotFoundException {
                        String path = name.replace('.', '/').concat(".class");
                        // 读取字节类资源
                        Resource res = ucp.getResource(path, false);
                        if (res != null) {
                            try {
                                return defineClass(name, res);
                            } catch (IOException e) {
                                throw new ClassNotFoundException(name, e);
                            }
                        } else {
                            return null;
                        }
                    }
                }, acc);
        } catch (java.security.PrivilegedActionException pae) {
            throw (ClassNotFoundException) pae.getException();
        }
        if (result == null) {
            throw new ClassNotFoundException(name);
        }
        return result;
    }

    /*
     * Retrieve the package using the specified package name.
     * If non-null, verify the package using the specified code
     * source and manifest.
     */
    private Package getAndVerifyPackage(String pkgname,
                                        Manifest man, URL url) {
        Package pkg = getPackage(pkgname);
        if (pkg != null) {
            // Package found, so check package sealing.
            if (pkg.isSealed()) {
                // Verify that code source URL is the same.
                if (!pkg.isSealed(url)) {
                    throw new SecurityException(
                        "sealing violation: package " + pkgname + " is sealed");
                }
            } else {
                // Make sure we are not attempting to seal the package
                // at this code source URL.
                if ((man != null) && isSealed(pkgname, man)) {
                    throw new SecurityException(
                        "sealing violation: can't seal package " + pkgname +
                        ": already loaded");
                }
            }
        }
        return pkg;
    }

    // Also called by VM to define Package for classes loaded from the CDS
    // archive
    private void definePackageInternal(String pkgname, Manifest man, URL url)
    {
        if (getAndVerifyPackage(pkgname, man, url) == null) {
            try {
                if (man != null) {
                    definePackage(pkgname, man, url);
                } else {
                    definePackage(pkgname, null, null, null, null, null, null, null);
                }
            } catch (IllegalArgumentException iae) {
                // parallel-capable class loaders: re-verify in case of a
                // race condition
                if (getAndVerifyPackage(pkgname, man, url) == null) {
                    // Should never happen
                    throw new AssertionError("Cannot find package " +
                                             pkgname);
                }
            }
        }
    }

    /*
     * Defines a Class using the class bytes obtained from the specified
     * Resource. The resulting Class must be resolved before it can be
     * used.
     */
    private Class<?> defineClass(String name, Resource res) throws IOException {
        long t0 = System.nanoTime();
        int i = name.lastIndexOf('.');
        URL url = res.getCodeSourceURL();
        if (i != -1) {//存在包名，定义包名
            String pkgname = name.substring(0, i);
            Manifest man = res.getManifest();
            // 判断是未缓存的包名，加入当前系统类加载器的packages集合中里
            definePackageInternal(pkgname, man, url);
        }
        // 读取二进制字节流，调用父类ClassLoader的defineClass
        java.nio.ByteBuffer bb = res.getByteBuffer();
        if (bb != null) {
            // Use (direct) ByteBuffer:
            CodeSigner[] signers = res.getCodeSigners();
            // 创建CodeSource，提供给SecureClassLoader的defineClass使用
            CodeSource cs = new CodeSource(url, signers);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, bb, cs);
        } else {
            byte[] b = res.getBytes();
            // must read certificates AFTER reading bytes.
            CodeSigner[] signers = res.getCodeSigners();
            // 创建CodeSource，提供给SecureClassLoader的defineClass使用
            CodeSource cs = new CodeSource(url, signers);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, b, 0, b.length, cs);
        }
    }

    // 从清单中提取信息，最后父类加载器 ClassLoader的definePackage方法
    protected Package definePackage(String name, Manifest man, URL url)
        throws IllegalArgumentException
    {
        String specTitle = null, specVersion = null, specVendor = null;
        String implTitle = null, implVersion = null, implVendor = null;
        String sealed = null;
        URL sealBase = null;

        Attributes attr = SharedSecrets.javaUtilJarAccess()
                .getTrustedAttributes(man, name.replace('.', '/').concat("/"));
        if (attr != null) {
            specTitle   = attr.getValue(Name.SPECIFICATION_TITLE);
            specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            specVendor  = attr.getValue(Name.SPECIFICATION_VENDOR);
            implTitle   = attr.getValue(Name.IMPLEMENTATION_TITLE);
            implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            implVendor  = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            sealed      = attr.getValue(Name.SEALED);
        }
        attr = man.getMainAttributes();
        if (attr != null) {
            if (specTitle == null) {
                specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
            }
            if (specVersion == null) {
                specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            }
            if (specVendor == null) {
                specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
            }
            if (implTitle == null) {
                implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
            }
            if (implVersion == null) {
                implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            }
            if (implVendor == null) {
                implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            }
            if (sealed == null) {
                sealed = attr.getValue(Name.SEALED);
            }
        }
        if ("true".equalsIgnoreCase(sealed)) {
            sealBase = url;
        }
        // 调用父类ClassLoader的definePackage，存放在packages集合中
        return definePackage(name, specTitle, specVersion, specVendor,
                             implTitle, implVersion, implVendor, sealBase);
    }


    // 判断给定包是否是密封包
    private boolean isSealed(String name, Manifest man) {
        // 先判断可信的属性
        Attributes attr = SharedSecrets.javaUtilJarAccess()
                .getTrustedAttributes(man, name.replace('.', '/').concat("/"));
        String sealed = null;
        if (attr != null) {
            sealed = attr.getValue(Name.SEALED);
        }
        // 再判断主要属性
        if (sealed == null) {
            if ((attr = man.getMainAttributes()) != null) {
                sealed = attr.getValue(Name.SEALED);
            }
        }
        return "true".equalsIgnoreCase(sealed);
    }

    // 根据名字+ URLClassPath查找对应资源的URL
    public URL findResource(final String name) {
        /*
         * The same restriction to finding classes applies to resources
         */
        URL url = AccessController.doPrivileged(
            new PrivilegedAction<URL>() {
                public URL run() {
                    return ucp.findResource(name, true);
                }
            }, acc);

        return url != null ? ucp.checkURL(url) : null;
    }

    // 查找所有URL
    public Enumeration<URL> findResources(final String name)
        throws IOException
    {
        final Enumeration<URL> e = ucp.findResources(name, true);

        return new Enumeration<URL>() {
            private URL url = null;

            private boolean next() {
                if (url != null) {
                    return true;
                }
                do {
                    URL u = AccessController.doPrivileged(
                        new PrivilegedAction<URL>() {
                            public URL run() {
                                if (!e.hasMoreElements())
                                    return null;
                                return e.nextElement();
                            }
                        }, acc);
                    if (u == null)
                        break;
                    url = ucp.checkURL(u);
                } while (url == null);
                return url != null;
            }

            public URL nextElement() {
                if (!next()) {
                    throw new NoSuchElementException();
                }
                URL u = url;
                url = null;
                return u;
            }

            public boolean hasMoreElements() {
                return next();
            }
        };
    }

    // 根据CodeSource获取对应权限 ，加入PermissionCollection perms
    protected PermissionCollection getPermissions(CodeSource codesource)
    {
        PermissionCollection perms = super.getPermissions(codesource);

        URL url = codesource.getLocation();

        Permission p;
        URLConnection urlConnection;

        try {
            urlConnection = url.openConnection();
            p = urlConnection.getPermission();
        } catch (java.io.IOException ioe) {
            p = null;
            urlConnection = null;
        }

        if (p instanceof FilePermission) {
            String path = p.getName();
            if (path.endsWith(File.separator)) {//目录文件权限，添加读权限
                path += "-";
                p = new FilePermission(path, SecurityConstants.FILE_READ_ACTION);
            }
        } else if ((p == null) && (url.getProtocol().equals("file"))) {//添加文件的读权限
            String path = url.getFile().replace('/', File.separatorChar);
            path = ParseUtil.decode(path);
            if (path.endsWith(File.separator))//路径以/结尾，需要改成 /-
                path += "-";
            p =  new FilePermission(path, SecurityConstants.FILE_READ_ACTION);
        } else {
            URL locUrl = url;
            if (urlConnection instanceof JarURLConnection) {
                locUrl = ((JarURLConnection)urlConnection).getJarFileURL();
            }
            String host = locUrl.getHost();
            if (host != null && (host.length() > 0))//存在host，添加socket connecton权限
                p = new SocketPermission(host,
                                         SecurityConstants.SOCKET_CONNECT_ACCEPT_ACTION);
        }
        // 存在SM时，判断创建者是否对该文件有权限
        if (p != null) {
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                final Permission fp = p;
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() throws SecurityException {
                        sm.checkPermission(fp);
                        return null;
                    }
                }, acc);
            }
            perms.add(p);//添加进集合并返回
        }
        return perms;
    }
    // 静态方法
    // 工厂方式创建URLClassLoader,需要安全检查
    // 创建 带给定父类加载器的 URLClassLoader
    public static URLClassLoader newInstance(final URL[] urls,
                                             final ClassLoader parent) {
        final AccessControlContext acc = AccessController.getContext();
        URLClassLoader ucl = AccessController.doPrivileged(
            new PrivilegedAction<URLClassLoader>() {
                public URLClassLoader run() {
                    return new FactoryURLClassLoader(urls, parent, acc);
                }
            });
        return ucl;
    }

    // 创建 带默认父类加载器的 URLClassLoader
    public static URLClassLoader newInstance(final URL[] urls) {
        // 取调用者的访问上下文acc
        final AccessControlContext acc = AccessController.getContext();
        // 需要一个有权限的代码块，通过工厂创建URLClassLoader
        URLClassLoader ucl = AccessController.doPrivileged(
            new PrivilegedAction<URLClassLoader>() {
                public URLClassLoader run() {
                    return new FactoryURLClassLoader(urls, acc);
                }
            });
        return ucl;
    }

    static {
        /*为了不报错，暂时注释掉，但是此处代码有用，供子类AppClassLoader/ExtClassLoader 使用
        sun.misc.SharedSecrets.setJavaNetAccess (
            new sun.misc.JavaNetAccess() {
                public URLClassPath getURLClassPath (URLClassLoader u) {
                    return u.ucp;
                }

                public String getOriginalHostName(InetAddress ia) {
                    return ia.holder.getOriginalHostName();
                }
            }
        );
        */
        // 设置并行能力
        ClassLoader.registerAsParallelCapable();
    }
}

final class FactoryURLClassLoader extends URLClassLoader {
    //注册成为 并行能力 类加载器 ，父类SecureClassLoader/ClassLoader都是并行能力 类加载器
    static {
        ClassLoader.registerAsParallelCapable();
    }
    //私有构造器
    FactoryURLClassLoader(URL[] urls, ClassLoader parent,AccessControlContext acc) {
        super(urls, parent, acc);
    }
    FactoryURLClassLoader(URL[] urls, AccessControlContext acc) {
        super(urls, acc);
    }

    public final Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {

        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {//sm不为null，检查包里类是否允许 被访问
            int i = name.lastIndexOf('.');
            if (i != -1) {
                sm.checkPackageAccess(name.substring(0, i));
            }
        }
        return super.loadClass(name, resolve);
    }
}
