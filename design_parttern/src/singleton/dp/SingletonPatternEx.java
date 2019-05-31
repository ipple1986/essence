package singleton.dp;

import singleton.dp.innerclass.InnerHolderClassSingleton;

public class SingletonPatternEx {
    public static void main(String ... args) {
       System.out.println(InnerHolderClassSingleton.name);
       System.out.println(InnerHolderClassSingleton.getInstance());
    //    System.out.println(ThreadUnsafedLazySingleton.name);
    }
}
