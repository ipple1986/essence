public class EndlessLoop {

    public static void main(String[] args) {

        /*whileTrue();
        System.out.println();
        forEmpty();
        System.out.println();
        forIIterator();*/


        // false
        System.out.printf("boolean default value " + bl + "\n");
        // ''
        System.out.printf("char default value " + c + "\n");
        // 0
        System.out.printf("byte default value " + b + "\n");
        System.out.printf("short default value " + s + "\n");
        System.out.printf("int default value " + i + "\n");
        System.out.printf("long default value " + l + "\n");
        // 0.0
        System.out.printf("float default value " + f + "\n");
        System.out.printf("double default value " + d + "\n");


        EndlessLoop endlessLoop  = new EndlessLoop();
        // false
        System.out.printf("boolean default value " + endlessLoop.blI + "\n");
        // ''
        System.out.printf("char default value " + endlessLoop.cI + "\n");
        // 0
        System.out.printf("byte default value " + endlessLoop.bI + "\n");
        System.out.printf("short default value " + endlessLoop.sI + "\n");
        System.out.printf("int default value " + endlessLoop.iI + "\n");
        System.out.printf("long default value " + endlessLoop.lI + "\n");
        // 0.0
        System.out.printf("float default value " + endlessLoop.fI + "\n");
        System.out.printf("double default value " + endlessLoop.dI + "\n");

    }
    private static void whileTrue(){
        while (true){}
    }
    private static void forEmpty(){
        for(;;){}
    }
    private static void forIIterator(){
        for(int i=0;i<1;i=0){}
    }


    private static byte b;
    private static short s;
    private static int i;
    private static long l;
    private static float f;
    private static double d;
    private static boolean bl;
    private static  char c;

    private byte bI;
    private short sI;
    private int iI;
    private long lI;
    private float fI;
    private double dI;
    private boolean blI;
    private  char cI;
}
