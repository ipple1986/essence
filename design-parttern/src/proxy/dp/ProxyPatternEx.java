package proxy.dp;

import proxy.dp.proxy.ProxySubject;

public class ProxyPatternEx {
    public static void main(String ...args){
        Subject subject = new ProxySubject();
        subject.doSomething();
    }
}
