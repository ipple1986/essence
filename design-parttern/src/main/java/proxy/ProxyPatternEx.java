package proxy;

public class ProxyPatternEx {
    public static void main(String ...args){
        ISubject subject = new ProxySubject();
        subject.doSomething();
    }
}
