package factoryMethod;

public class ConcreteFactory implements IAnimalFactory {
    @Override
    public IAnimal createAnimal(String type) throws Exception {
        switch (type){
            case "duck":return new Duck();
            case "tiger":return new Tiger();
            default: throw  new Exception(type + " cannot be instantiated");
        }
    }
}
