//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package javaapi.sun.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaIndex {
    private static volatile Map<File, MetaIndex> jarMap;
    private String[] contents;
    private boolean isClassOnlyJar;

    public static MetaIndex forJar(File var0) {
        return (MetaIndex)getJarMap().get(var0);
    }
    //将var0目录下的meta-index文件内部，写入JarMap(File,MetaIndex)映射中
    /*
! management-agent.jar
@ resources.jar
com/sun/java/util/jar/pack/
META-INF/services/sun.util.spi.XmlPropertiesProvider
......
com/sun/jndi/
com/sun/org/
META-INF/services/javax.sound.sampled.spi.FormatConversionProvider
! rt.jar
com/sun/java/util/jar/pack/
java/
......
com/sun/java/browser/
com/sun/corba/
     */
    public static synchronized void registerDirectory(File var0) {
        File var1 = new File(var0, "meta-index");
        if (var1.exists()) {
            try {
                BufferedReader var2 = new BufferedReader(new FileReader(var1));
                String var3 = null;
                String var4 = null;
                boolean var5 = false;
                ArrayList var6 = new ArrayList();//存放结果
                Map var7 = getJarMap();//File->MetaIndex映射MAP
                var0 = var0.getCanonicalFile();
                var3 = var2.readLine();
                //第一行是"% VERSION 2",才继续往下走
                if (var3 == null || !var3.equals("% VERSION 2")) {
                    var2.close();
                    return;
                }

                while((var3 = var2.readLine()) != null) {//读取meta-index第一行
                    /*
                    ! name
                    a.jar
                    [#|@] name
                    aabb
                    具体例子看上面
                     */
                    switch(var3.charAt(0)) {
                    case '!':
                    case '#':
                    case '@':
                        if (var4 != null && var6.size() > 0) {
                            //第二次碰到 !#@时，并且收集集合为空
                            //说明前面全是 !#@
                            var7.put(new File(var0, var4), new MetaIndex(var6, var5));
                            var6.clear();
                        }

                        var4 = var3.substring(2);//记录最近一个!#@,后面的字符串
                        if (var3.charAt(0) == '!') {
                            var5 = true;
                        } else if (var5) {
                            var5 = false;
                        }
                    case '%'://注释，跳过
                        break;
                    default://其它字符串，加入
                        var6.add(var3);
                    }
                }
                //收尾
                if (var4 != null && var6.size() > 0) {
                    var7.put(new File(var0, var4), new MetaIndex(var6, var5));
                }
                //关流
                var2.close();
            } catch (IOException var8) {
            }
        }

    }

    public boolean mayContain(String var1) {
        if (this.isClassOnlyJar && !var1.endsWith(".class")) {
            return false;
        } else {
            String[] var2 = this.contents;

            for(int var3 = 0; var3 < var2.length; ++var3) {
                if (var1.startsWith(var2[var3])) {
                    return true;
                }
            }

            return false;
        }
    }

    private MetaIndex(List<String> var1, boolean var2) throws IllegalArgumentException {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            this.contents = (String[])var1.toArray(new String[0]);
            this.isClassOnlyJar = var2;
        }
    }

    private static Map<File, MetaIndex> getJarMap() {
        if (jarMap == null) {
            Class var0 = MetaIndex.class;
            synchronized(MetaIndex.class) {
                if (jarMap == null) {
                    jarMap = new HashMap();
                }
            }
        }

        assert jarMap != null;

        return jarMap;
    }
}
