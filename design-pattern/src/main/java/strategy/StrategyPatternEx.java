package strategy;

public class StrategyPatternEx {

    public static void main(String ... args){
        Context ctx  = new Context();

        IChoice choice1 = new FirstChoice();
        IChoice choice2 = new SecondChoice();

        String a = "1";
        String b = "2";

        //set firstChoice
        ctx.setChoice(choice1);
        ctx.showChoice(a,b);
        //change to secondChoice
        ctx.setChoice(choice2);
        ctx.showChoice(a,b);
    }
}
