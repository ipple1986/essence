import java.io.IOException;
import java.net.Socket;

public class BinarySearch {

    public static void main(String[] args) {
        Integer[] A = new Integer[]{ 1,3,6,9,13,17,20,23,26,30,35,38,41,46};
        Integer key = 1;
        nonRecurisive(A,key);
        recurisive(A,key,0,A.length-1);


        A = new Integer[]{23};
        nonRecurisive(A,23);
        recurisive(A,23,0,A.length-1);


    }
    private static void nonRecurisive(Integer[] A,int key){
        int low = 0,high = A.length-1;
        while(low <= high){
            int mid  = (low + high)/2;
            if(A[mid] == key){
                System.out.println("Found it!");
                return ;
            }
            if(A[mid] > key){
                high = mid - 1;
            }else {
                low = mid + 1;
            }
        }
        System.out.println("Not Found!");
    }
    private static void recurisive(Integer[] A,int key,int low,int high){
        int mid = (low+high)/2;
        if(low > high){
            System.out.println("Not Found!!");
            return;
        }
        if(A[mid] == key ) {
            System.out.println("Found it!!");
            return;
        }
        if(A[mid] > key){
            recurisive(A,key,low,mid-1);
        }else{
            recurisive(A,key,mid + 1,high);
        }
    }
}
