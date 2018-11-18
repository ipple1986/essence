public class Fibanacci {
    //O(n^2) 扇出=2
    public static int foo1( int n ){
        if( n < 3 )return n;
        return foo1(n-1) + foo1(n-2);
    }
    //O(n)
    public static int foo2( int n ){
        if( n < 3 )return n;
        int a= 1,b=2;
        for(int i=3;i <= n;i++){
            a = a + b;
            b = a - b;
            a = a - b;//三式兩數交換
            b = a + b;
        }
        return b;
    }
    //扇出=1
    public static int foo3(int a ,int b,int i,int n){
        if( n < 3 )return n;
        if(i==n)return a + b;
        return foo3(b,a+b,i+1, n);
    }
    //公式大法 O(1)
    public static int foo4(int n){
        //S^2 - S - 1 = 0
        //(x+a)(x+b)=0
        //ab=-1
        //a+b=-1
        //a(1+a)=1

        return 0;
    }
    public static void main(String args[]){
        System.out.println(foo1(2));
        System.out.println(foo2(2));
        System.out.println(foo3(1,2,3,2));
    }
}