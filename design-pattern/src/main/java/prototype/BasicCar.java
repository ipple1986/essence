package prototype;

import java.util.Random;

public abstract class BasicCar implements Cloneable{
    public static int setPrice(){
        Random random = new Random();
        return  random.nextInt(100000);
    }
    public String modelname;
    public int price;
    public BasicCar clone() throws CloneNotSupportedException{
        return (BasicCar)super.clone();
    }
}
