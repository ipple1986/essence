package proxy;

public class ProxySubject implements ISubject {
    private ConcreteSubject subject = new ConcreteSubject();
    @Override
    public void doSomething() {
        subject.doSomething();
    }
}
