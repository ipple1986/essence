package prototype;

public class VehiclePrototypeExample {
    public static void main(String[] args) throws CloneNotSupportedException {
        BasicCar ford = new Ford("ford",100000);
        BasicCar nano = new Nano("ford",150000);

        System.out.println(ford.modelname+" "+ford.price);
        System.out.println(nano.modelname+" "+nano.price);

        System.out.println("==============");
        BasicCar ford1  = ford.clone();
        ford1.price = ford1.price+ BasicCar.setPrice();
        BasicCar nano1  = nano.clone();
        nano1.price = nano1.price+ BasicCar.setPrice();

        System.out.println(ford1.modelname+" "+ford1.price);
        System.out.println(nano1.modelname+" "+nano1.price);
    }
}
