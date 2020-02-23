package javaapi.sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

public class VMSupport {
    private static Properties agentProps = null;

    public VMSupport() {
    }
    //获取代理的配置 Properties
    public static synchronized Properties getAgentProperties() {
        if (agentProps == null) {
            agentProps = new Properties();
            initAgentProperties(agentProps);//调用底层initAgentProperties方法 初始化 agentProps
        }

        return agentProps;
    }

    private static native Properties initAgentProperties(Properties var0);

    //让Properties配置转成字节数组
    private static byte[] serializePropertiesToByteArray(Properties var0) throws IOException {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream(4096);
        Properties var2 = new Properties();
        Set var3 = var0.stringPropertyNames();
        Iterator var4 = var3.iterator();

        while(var4.hasNext()) {
            String var5 = (String)var4.next();
            String var6 = var0.getProperty(var5);
            var2.put(var5, var6);
        }

        var2.store(var1, (String)null);
        return var1.toByteArray();
    }
    //将系统配置 序列化成 字节数组
    public static byte[] serializePropertiesToByteArray() throws IOException {
        return serializePropertiesToByteArray(System.getProperties());
    }
    //将代理配置 序列化成 字节数组
    public static byte[] serializeAgentPropertiesToByteArray() throws IOException {
        return serializePropertiesToByteArray(getAgentProperties());
    }
    //判断给定jar文件是不是包含Class-Path:字段
    public static boolean isClassPathAttributePresent(String var0) {
        try {
            Manifest var1 = (new JarFile(var0)).getManifest();
            return var1 != null && var1.getMainAttributes().getValue(Name.CLASS_PATH) != null;
        } catch (IOException var2) {
            throw new RuntimeException(var2.getMessage());
        }
    }
    //调底层获取 VM临时目录
    public static native String getVMTemporaryDirectory();
}
