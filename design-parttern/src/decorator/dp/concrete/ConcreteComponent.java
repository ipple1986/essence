package decorator.dp.concrete;

import decorator.dp.Component;

public class ConcreteComponent implements Component {
    @Override
    public void doWork() {
        System.out.println("invoke ConcreteComponent doWork method");
    }
}
