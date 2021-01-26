import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

public class InstrumentProxy {
    public static void agentmain(String agentArgs,Instrumentation instrumentation){
        System.out.println("2222");
        try {
            instrumentation.appendToSystemClassLoaderSearch(new JarFile(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void premain(String agentArgs,Instrumentation instrumentation){

        System.out.println(agentArgs+ " === "+ instrumentation);

        System.out.println("isRedefineClassesSupported "+ instrumentation.isRedefineClassesSupported());
        System.out.println("isRetransformClassesSupported "+instrumentation.isRetransformClassesSupported());
        Class[] classes = instrumentation.getAllLoadedClasses();
        for(Class clz:classes){
            //System.out.println(clz.getName());
        }

        try {
            instrumentation.retransformClasses(String.class);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }
}
