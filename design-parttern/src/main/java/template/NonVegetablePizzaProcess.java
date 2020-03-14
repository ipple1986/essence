package template;

public class NonVegetablePizzaProcess extends AbstratPizzaProcess {
    @Override
    protected void putVegetableIfNeed() {
        System.out.println("不需要添加蔬菜");
    }
}
