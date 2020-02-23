import java.util.ArrayList;
import java.util.List;

public class Chapter3 {
//等待与通知，生产者/消费者
    public static void main(String ...args){
        //等待与通知，生产者/消费者
        //notify/notifyAll/wait + 带时间的notifiy/notifiyAll/wait
        //wait 接收中断或带超时待时间，一旦处于等待对象锁中，后面代码不会被执行，超时继续执行后面代码
        //nofify/notifyAll 执行后，后面代码执行完再释放锁
        //nofify/wait方法必要放于同步块/同步方法中
        //wait方法必须包在loop循环中，否则有可能notify先于obj.wait()，导致wait等待永远无法被唤醒
        /*
        synchronized(obj){
            while (<condition does not hold>)//为了保持程序的活性
                obj.wait();
            // Perform an action that's appropriate to condition.
        }
        synchronized(obj){
            // Set the condition.
            obj.notify();
        }
         */
        //生产者消费者
        List<Character> characterList = new ArrayList<>();
        new Comsumer(characterList).start();
        new Producer(characterList).start();
    }
    static volatile  boolean productable = Boolean.TRUE;
    static class Comsumer extends  Thread{
        private List<Character> characterList;
        public Comsumer(List<Character> characterList){
            this.characterList = characterList;
        }
        @Override
        public void run() {
            synchronized (characterList){
                Character character;
                do {
                    while (productable) {
                        try {
                            characterList.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    character = characterList.remove(0);
                    System.out.println(String.format("消费:%s", character));

                    productable = Boolean.TRUE;
                    characterList.notify();
                }while(character!='Z');
            }
        }
    }

    static class Producer extends  Thread{
        private List<Character> characterList;
        private char character = 'A';
        public Producer(List<Character> characterList){
            this.characterList = characterList;
        }
        @Override
        public void run() {
            synchronized (characterList){
                do{
                    while(!productable) {
                        try {
                            characterList.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    characterList.add(character);
                    System.out.println(String.format("生产:%s",character));
                    productable = Boolean.FALSE;
                    characterList.notify();
                    character++;
                }while (character<='Z');


            }
        }
    };
}
