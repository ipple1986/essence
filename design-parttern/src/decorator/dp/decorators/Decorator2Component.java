package decorator.dp.decorators;

public class Decorator2Component extends AbstractDecoratorComponent {
    @Override
    public void doWork() {
        System.out.println("Decorator2Component begin doWork()");
        super.doWork();
        System.out.println("Decorator2Component end doWork()");
    }
}
