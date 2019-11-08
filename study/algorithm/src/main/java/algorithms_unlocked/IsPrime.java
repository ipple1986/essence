package algorithms_unlocked;

// 正确性：针对问题得到合理的答案/近似算法（特定因素下的最佳方案为正确）
// 有效性：时间/内存/网络/磁盘/随机数
// 针对时间有影响因子：计算机速度/编程语言/编译解析器/编程人员水平/其他
// 输入规模+增长率

// Introduction to Algorithms [CLRS09] by four
//John MacCormick’s book Nine Algorithms That Changed the Future[Mac12]
//The Art of Computer Programming [Knu97, Knu98a, Knu98b, Knu11]
public class IsPrime {
    public static void main(String[] args) {
        int num = 18;
        isPrime(num);
        IsPrimeBetter(num);
    }
    //筛选法
    private static void isPrime(int num){
        for(int i=2;i<num;i++){
            if(num%i==0){
                System.out.println("not prime");
                return;
            }
        }
        System.out.println("is prime");
    }
    // 优化筛选法
    private  static void IsPrimeBetter(int num){
        // 一个整数n能被a整除，那么n/a也能被n整除
        // n=√n * √n 即 如果a在[1,√n]中，那么(n/b)就在[√n,n]中，反之亦然
        for(int i=2;i<=Math.sqrt(num);i++){
            if(num%i==0){
                System.out.println("not prime");
                return;
            }
        }
        System.out.println("is prime");
    }
}
