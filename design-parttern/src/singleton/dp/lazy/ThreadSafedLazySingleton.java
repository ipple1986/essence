package singleton.dp.lazy;

public class ThreadSafedLazySingleton {
    private static ThreadSafedLazySingleton instance;
    private ThreadSafedLazySingleton(){
        System.out.println("ThreadSafedLazySingleton Constructor");
    }

    public synchronized  static ThreadSafedLazySingleton getInstance(){
        if(instance==null){
            instance = new ThreadSafedLazySingleton();
        }
        return instance;
    }
}
