
public class 浩鲸科技 {

    //sorting the int array
    private static void printIntArray(String prefix,int[] A){
        if(prefix!=null)System.out.print(prefix.concat(":"));
        for(int i=0;i<A.length;i++){
            System.out.print(A[i] +" ");
        }
        System.out.println();
    }
    private static void sortIntArray(){
        int[] A = new int[]{ 13, 2, 7,12, 18, 21, 2, 25, 22, 19, 28};

        printIntArray("排序前",A);

        for(int i=0;i<A.length;i++){
            int j = i-1;
            int key = A[i];
            while(j>=0 &&  A[j] > key){
                A[j+1] = A[j];
                j--;
            }
            j++;
            if(i!=j){
                A[j] = key;
            }
        }
        printIntArray("排序后",A);
    }
    //Math题 十进数判断是否对称数
    private static void checkSymmetryNubmer(){
        // true
        int checkNum = 123321;
        // false
        //int checkNum = 123231;
        int reverseNum = 0;
        int orginNum = checkNum;
        while(checkNum!=0){
            reverseNum = reverseNum*10 + checkNum%10;
            checkNum/=10;
        }
        System.out.println(orginNum == reverseNum);

    }
    public static void main(String[] args) {
        // 整形数组排序
        sortIntArray();

        // 判断对称数
        //checkSymmetryNubmer();

        //测试静态块与构造器的执行顺序
        //testConstrutorAndStaticBlockExecuteOrder();
    }
    // 静态块/静态字段 优先于构造器 执行
    // 静态块按照定义的顺序执行，并且只执行一次
    private static void testConstrutorAndStaticBlockExecuteOrder(){
        B bb = new B();// 12ab
        bb = new B();//ab

        // StringBuffer线程安全 StringBuilder非线程安全，String不可变字符串
    }

    private static void IE和FirefoxJs的区别至少三点(){
        /*
            reference:https://blog.csdn.net/hcmfys2009/article/details/83265843
            Form表单获取: IE: document.forms("formName")   Firefox: document.forms["formId"]
            HTML Dom元素获取：IE：document.all("itemId")  Firefox:document.getElementById("itemId")
            修改DIV样式，DivObject.style.display="none" Firefox:document.getElementById("divId").style.display="none"
            打开窗口：IE有模态/非模态窗口函数， FF没有，只能通过window.open打开

         */

    }
    private static void Js实现面向对象(){
        /*
            reference:https://www.cnblogs.com/chaixiaozhi/p/8515087.html

            构造器继承apply/call
            function Animal(hello){
                this.hello = hello;
                this.sayHello = function(){
                    return this.hello
                }
            }
            Animal.prototype.fuck = function(){ return "fucking" }//子类Person无法继承

            function Person(name,age,hello){
                this.name = name;
                this.age = age;
                Animal.call(this,hello);
                this.sayHello = function(){
                    return this.hello +',My Name is '+ this.name +",i'am "+this.age+" years old.";
                }
            }
         */
    }
}
class A{

    static{
        System.out.println("1");
    }

    public A(){
        System.out.println("a");
    }
}
class B extends  A{

    private static C c = new C();
    static{
        System.out.println("2");
    }
    public B(){
        System.out.println("b");
    }
}
class C{
    static{
        System.out.println("3");
    }
    public C(){
        System.out.println("c");
    }

}