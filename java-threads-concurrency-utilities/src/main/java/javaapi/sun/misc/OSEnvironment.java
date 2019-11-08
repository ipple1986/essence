//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package javaapi.sun.misc;

import sun.io.Win32ErrorMode;

public class OSEnvironment {
    public OSEnvironment() {
    }
    //具体平台，这里实现不一样
    public static void initialize() {
        Win32ErrorMode.initialize();
    }
}
