package singlejvm.mutiplethreads;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.util.Scanner;

public class FileLock{
    public java.nio.channels.FileLock lock(String lockFile){
        FileOutputStream fileOutputStream;
        try{
            fileOutputStream = new FileOutputStream(lockFile);
            FileChannel fileChannel = fileOutputStream.getChannel();
            try {
                return fileChannel.tryLock(0,1000L,false);
            }catch (OverlappingFileLockException o){
                return  null;
            }
        }catch (FileNotFoundException e){
            try{
                new File(lockFile).createNewFile();
                return null;
            }catch (IOException el){
                return null;
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }
    public void releaseLock(java.nio.channels.FileLock fileLock){
        try {
            if(fileLock!=null){
                synchronized (this){
                    if(fileLock!=null ){
                        fileLock.close();
                    }
                }
            }
        }catch (IOException e){}
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if(line.contains("+")){
                String[] n = line.split("\\+");
                double a = Double.valueOf(n[0]);
                double b = Double.valueOf(n[1]);
                System.out.printf("%.4f+%.4f=%.4f\n",a,b,a+b);
            }else if(line.indexOf("-")>=0){
                String[] n = line.split("-");
                double a = Double.valueOf(n[0]);
                double b = Double.valueOf(n[1]);
                System.out.printf("%.4f-%.4f=%.4f\n",a,b,a-b);
            }else if(line.indexOf("*")>=0){
                String[] n = line.split("*");
                double a = Double.valueOf(n[0]);
                double b = Double.valueOf(n[1]);
                System.out.printf("%.4f*%.4f=%.4f\n",a,b,a*b);
            }else if(line.indexOf("/")>=0){
                String[] n = line.split("/");
                double a = Double.valueOf(n[0]);
                double b = Double.valueOf(n[1]);
                System.out.printf("%.4f/%.4f=%.4f\n",a,b,a/b);
            }else{
                System.out.println("Invalid operation!");
            }

        }
    }
}