package templateMethod.dp;

public  abstract  class AbstratPizzaProcess {
    //template method here
    public void makePizza(){
        prepare();
        putMeat();
        putVegetableIfNeed();
        last();
    }
    //制作比萨前操作
    public void prepare(){
        System.out.println("制作比萨前操作");
    }
    //添加肉类
    public void putMeat(){
        System.out.println("添加肉类");
    }
    protected abstract void  putVegetableIfNeed();

    //制作pizza最后的操作
    public void last(){
        System.out.println("制作pizza最后的操作");
    }
}
