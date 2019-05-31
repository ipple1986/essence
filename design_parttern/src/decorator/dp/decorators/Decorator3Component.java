package decorator.dp.decorators;

public class Decorator3Component extends AbstractDecoratorComponent {
    @Override
    public void doWork() {
        System.out.println("Decorator3Component begin doWork()");
        super.doWork();
        System.out.println("Decorator3Component end doWork()");
    }
}
