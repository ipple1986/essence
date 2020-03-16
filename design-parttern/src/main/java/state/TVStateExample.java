package state;

public class TVStateExample {
    public static void main(String[] args) {
        Off  initialState = new Off();
        TV tv = new TV(initialState);
        //first
        tv.pressBusston();
        //second
        tv.pressBusston();
        //third
        tv.pressBusston();
    }
}
