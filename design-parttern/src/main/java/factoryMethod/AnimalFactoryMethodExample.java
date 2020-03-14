package factoryMethod;

public class AnimalFactoryMethodExample {
    public static void main(String[] args) throws Exception {
        ConcreteFactory concreteFactory = new ConcreteFactory();

        IAnimal duck = concreteFactory.createAnimal("duck");
        duck.speak();

        IAnimal tiger = concreteFactory.createAnimal("tiger");
        tiger.speak();

        IAnimal lion = concreteFactory.createAnimal("lion");
    }
}
