import java.lang.System;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
           String line = in.nextLine();
            if(line.contains("+")){
               String[] n = line.split("\\+");
               double a = Double.valueOf(n[0]);
               double b = Double.valueOf(n[1]);
                if(b==0.0){
                    System.out.println("Wrong!Division by zero!");
                }else{
               System.out.printf("%.4f+%.4f=%.4f\n",a,b,a+b);                    
                }

            }else if(line.indexOf("-")>=0){
String[] n = line.split("-");
               double a = Double.valueOf(n[0]);
               double b = Double.valueOf(n[1]);
               
                if(b==0.0){
                    System.out.println("Wrong!Division by zero!");
                }else{
                System.out.printf("%.4f-%.4f=%.4f\n",a,b,a-b);
                }
               }else if(line.indexOf("*")>=0){
String[] n = line.split("*");
               double a = Double.valueOf(n[0]);
               double b = Double.valueOf(n[1]);
               
                if(b==0.0){
                    System.out.println("Wrong!Division by zero!");
                }else{
                System.out.printf("%.4f*%.4f=%.4f\n",a,b,a*b);
                }
                }else if(line.indexOf("/")>=0){
String[] n = line.split("/");
               double a = Double.valueOf(n[0]);
               double b = Double.valueOf(n[1]);
               
                if(b==0.0){
                    System.out.println("Wrong!Division by zero!");
                }else{
                System.out.printf("%.4f/%.4f=%.4f\n",a,b,a/b);
                }
                }else{
                System.out.println("Invalid operation!");
            }
            
        }
    }
}