package proxy.dp.original;

import proxy.dp.Subject;

public class OriginalSubject implements Subject {
    @Override
    public void doSomething() {
        System.out.println("Concrete Object do Something");
    }
}
