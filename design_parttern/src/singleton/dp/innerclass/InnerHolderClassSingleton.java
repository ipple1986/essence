package singleton.dp.innerclass;

public class InnerHolderClassSingleton {
    public static final String name = "InnerHolderClassSingleton";
    private InnerHolderClassSingleton(){
        System.out.println("InnerHolderClassSingleton Constructor");
    }
    public static class SingletonHolder{
      private final  static InnerHolderClassSingleton innerSignleton= new InnerHolderClassSingleton();
    }
    public static InnerHolderClassSingleton getInstance(){
        return SingletonHolder.innerSignleton;
    }
}
