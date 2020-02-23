public class Fibonacci {
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
        //a*x^2+b*x+c=0,x1= (-b + sqrt(b^2-4ac)/2a,x2= (-b - sqrt(b^2-4ac)/2a
        //S1= (1+sqrt(5))/2, s2=(1-sqrt(5))/2
        //代入 An = C1*(S1)^n + C2*(S2)^n,A1=1 A2=2
        //得到 C1,C2
        //C1 = (S2-2)/(S1S2-S1^2) = (3 + sqrt(5))/ (5 + sqrt(5))
        //C2 = (S1-2)/(S1S2-S2^2) = (3 - sqrt(5))/ (5 + sqrt(5))
        //最後 An = ((3 + sqrt(5))/ (5 + sqrt(5)))*((1+sqrt(5))/2)^n - ((3 - sqrt(5))/ (5 + sqrt(5)))*((1-sqrt(5))/2)^n
        double c = Math.sqrt(5.0);
        double c1 = (3 + c)/ (5 + c);
        double c2 = (3 - c)/ (5 + c);

        return (int)Math.round((c1)*Math.pow((1+c)/2,n) - (c2)*Math.pow((1-c)/2,n));

    }
    public static int foo5(int n){
        double c = Math.sqrt(5.0);
        return (int)Math.round((1/c)*Math.pow((1+c)/2,n+1)-(1/c)*Math.pow((1-c)/2,n+1));
    }
    public static void main(String args[]){
        System.out.println(foo1(13));
        System.out.println(foo2(13));
        System.out.println(foo3(1,2,3,13));
        System.out.println(foo4(13));
        System.out.println(foo5(13));
    }
}