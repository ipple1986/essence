package proxy;

public class ConcreteSubject implements ISubject {
    @Override
    public void doSomething() {
        System.out.println("Concrete Object do Something");
    }
}
