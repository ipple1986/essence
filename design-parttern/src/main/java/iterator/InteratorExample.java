package iterator;

public class InteratorExample {
    public static void main(String[] args) {
        Arts arts = new Arts();
        Science science = new Science();

        printing(arts.iterator());
        System.out.println("================");
        printing(science.iterator());

    }
    private static void printing(IIterator iIterator){
        while(!iIterator.isDone()){
            System.out.println(iIterator.next());
        }
    }
}
