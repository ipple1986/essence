package strategy.dp;

public class Context {
    private IChoice choice;
    public void showChoice(String a,String b){
        this.choice.myChoice(a, b);
    }
    public void setChoice(IChoice choice){
        this.choice = choice;
    }
}
