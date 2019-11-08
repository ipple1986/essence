import javax.annotation.Resource;
import javax.jws.WebService;
import java.util.Arrays;

public class TestAnnotation {
    public static  void main(String ...args){
        testAnnotation();
Runnable runnable;
runnable = new Runnable() {
    @Override
    public void run() {
    System.out.println("--");
    }
    Object i = 14;
    int j = (int)i;
    Number k = j;
};
runnable.run();

    }
    private static void testAnnotation(){
        Arrays.stream(A.class.getAnnotations()).forEach(k->System.out.println(k));
        Arrays.stream(A.class.getFields()).forEach(k->System.out.println(k));

    System.out.println("--------------");
        Arrays.stream(A.class.getDeclaredAnnotations()).forEach(k->System.out.println(k));
    }
    @Resource
    @Deprecated
    static class A extends B {
        @Resource
        private int age;
    }
    @WebService
    static class B  implements C{
        @Resource
        public  int age;
         public void method1(){};
    }
    @Resource
    interface C{

    }
}


