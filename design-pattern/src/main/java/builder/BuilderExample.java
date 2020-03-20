package builder;

public class BuilderExample {
    public static void main(String[] args) {

        Product car = Director.construct(new Car());
        car.show();

        Product motorcycle = Director.construct(new MotorCycle());
        motorcycle.show();
    }
}
