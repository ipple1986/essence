package singleton;

public class ThreadUnsafedLazySingleton {
    public static final String name = "ThreadUnsafedLazySingleton";
    private static ThreadUnsafedLazySingleton instance;
    private ThreadUnsafedLazySingleton(){
        System.out.println("ThreadUnsafedLazySingleton Constructor");
    }
    //unsafe-thread here
    public static ThreadUnsafedLazySingleton getInstance(){
        if(instance==null){
            instance = new ThreadUnsafedLazySingleton();
        }
        return instance;
    }
}
