package factoryMethod;

public interface IAnimalFactory {
    IAnimal createAnimal(String type) throws Exception;
}
