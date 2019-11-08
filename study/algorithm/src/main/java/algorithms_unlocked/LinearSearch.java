package algorithms_unlocked;

public class LinearSearch {

    public static void main(String[] args) {
        int[] A = {12,14,1,21,9,12,23,26};
        int x = 9;
        System.out.println(linearSearch(A,x));
        System.out.println(linearSearchBetter(A,x));
        System.out.println(linearSearchSentinel(A,x));
    }
    private static int linearSearch(int[] A,int x){
        int idx = -1;
        for(int i=0;i<A.length;i++){
            if(x == A[i])idx = i;
        }
        return idx;
    }
    // 两层判断，找到不继续往下查找
    private static int linearSearchBetter(int[] A,int x){
        for(int i=0;i<A.length;i++){
            if(x == A[i]){
                return i;
            }
        }
        return -1;
    }
    //哨兵线性查找，一层判断
    private static int linearSearchSentinel(int[] A,int x){
        int last = A[A.length-1];//暂存最后一位
        A[A.length-1] = x;
        int i=0;
        while(x!=A[i]){
            i++;
        }
        A[A.length-1] = last;//恢复最后一位
        if(i<A.length-2 || A[A.length-1] == x)return i;
        return -1;
    }
}
