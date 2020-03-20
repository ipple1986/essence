package builder;

public class MotorCycle implements IBuilder {
    private Product product = new Product();
    @Override
    public void buildBody() {
        product.add("this is a body of MotorCycle");
    }

    @Override
    public void addWheels() {
        product.add("has 2 wheels");
    }

    @Override
    public void addHeadlights() {
        product.add("has 1 headlights");
    }

    @Override
    public Product getVehicle() {
        return product;
    }
}
