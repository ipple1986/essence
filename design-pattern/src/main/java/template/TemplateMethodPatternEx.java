package template;

public class TemplateMethodPatternEx {
    public static void main(String ... args){
        new VegetablePizzaProcess().makePizza();
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        new NonVegetablePizzaProcess().makePizza();
    }
}
