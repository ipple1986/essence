package proxy.dp.proxy;

import proxy.dp.Subject;
import proxy.dp.original.OriginalSubject;

public class ProxySubject implements Subject {
    private OriginalSubject subject = new OriginalSubject();
    @Override
    public void doSomething() {
        subject.doSomething();
    }
}
