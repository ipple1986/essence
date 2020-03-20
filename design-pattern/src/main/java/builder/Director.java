package builder;

public class Director {
    public static Product construct(IBuilder iBuilder){
        iBuilder.buildBody();
        iBuilder.addWheels();
        iBuilder.addHeadlights();
        return iBuilder.getVehicle();
    }
}
