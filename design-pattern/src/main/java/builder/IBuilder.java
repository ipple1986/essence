package builder;

public interface IBuilder {
    void buildBody();
    void addWheels();
    void addHeadlights();
    Product getVehicle();
}
