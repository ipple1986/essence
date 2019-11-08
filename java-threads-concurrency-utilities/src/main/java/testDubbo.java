public class testDubbo {
    private static void testMethodName(){
        System.out.println(testDubbo.class.getDeclaredMethods()[1].getName());
    }
    public static void main(String... args){
        //testMethodName();
        //testSystemProperty();
    }
    public static void testSystemProperty(){
        //System.getenv("");// 系统环境变量
        //System.getProperty("");// OS相关变量
        System.getenv().entrySet().forEach(k->{
            System.out.println(k);
        });
        System.getProperties().entrySet().forEach(k->{
            System.out.println(k);
        });
    }

}
