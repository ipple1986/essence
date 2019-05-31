package strategy.dp;

public class FirstChoice implements IChoice {
    @Override
    public void myChoice(String a, String b) {
        Integer i = Integer.valueOf(a);
        Integer j = Integer.valueOf(b);
        System.out.println("sum: " + (i+j));//sum = ( a+b )
    }
}
