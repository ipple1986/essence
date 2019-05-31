package singleton.dp.eager;

public class EagerSingleton {
    public static final String name = "EagerSingleton";
    private static EagerSingleton instance = new EagerSingleton();
    private EagerSingleton(){
        System.out.println("EagerSingleton Constructor");
    }
    public static EagerSingleton getInstance(){
        return instance;
    }
}
