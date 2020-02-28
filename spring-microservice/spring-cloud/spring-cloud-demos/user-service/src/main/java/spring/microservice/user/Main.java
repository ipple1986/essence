package spring.microservice.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Main {
    public static void main(String ...args){
        /*System.out.println(System.getProperty("sun.boot.class.path"));
        System.out.println(System.getProperty("sun.io.allowCriticalErrorMessageBox"));
        System.out.println(System.getProperty("sun.boot.class.path"));
        System.out.println(System.getProperty("java.system.class.loader"));
        try {
            System.out.println("----------");
            System.out.println("".getClass().getDeclaredMethod("toString").getDeclaringClass().getClassLoader());
            System.out.println("----------");
            ///
            System.out.println(Application.class.getClassLoader());
            System.out.println(Application.class.getClassLoader().getSystemClassLoader());
            System.out.println(String.class.getClassLoader());
            System.out.println(String.class.getClassLoader().getSystemClassLoader());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        System.out.println("------------");*/
        A a = new A();
        try {
            Arrays.asList(a.getClass()).forEach(k->{
                System.out.println(k instanceof Class);
            });
            System.out.println(Arrays.asList(a.getClass().getAnnotations()));
            Arrays.asList(a.getClass().getDeclaredMethod("getAge")).forEach(k->{
                System.out.println(k instanceof Method);
                System.out.println(k);
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


}
@Deprecated
@Order
class A{
    @Autowired
    private int age;
    @Value("")
    public  int getAge(){
        return  age;
    }
}

