package builder;

public class Car implements IBuilder {
    private Product product = new Product();
    @Override
    public void buildBody() {
        product.add("this is a body of Car");
    }

    @Override
    public void addWheels() {
        product.add("has 4 wheels");
    }

    @Override
    public void addHeadlights() {
        product.add("has 2 headlights");
    }

    @Override
    public Product getVehicle() {
        return product;
    }
}
