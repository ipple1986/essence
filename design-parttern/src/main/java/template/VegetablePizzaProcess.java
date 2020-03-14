package template;

public class VegetablePizzaProcess extends AbstratPizzaProcess {
    @Override
    protected void putVegetableIfNeed() {
        System.out.println("添加蔬菜操作");
    }
}
