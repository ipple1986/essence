package strategy.dp;

public class SecondChoice implements IChoice {
    @Override
    public void myChoice(String a, String b) {
            System.out.println("concatenate :"+ a + b);//ab
    }
}
