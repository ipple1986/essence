package decorator.dp.decorators;

public class Decorator1Component extends AbstractDecoratorComponent {
    @Override
    public void doWork() {
        System.out.println("Decorator1Component begin doWork()");
        super.doWork();
        System.out.println("Decorator1Component end doWork()");
    }
}
