package templateMethod.dp;

import templateMethod.dp.nonvegetale.NonVegetablePizzaProcess;
import templateMethod.dp.vegetable.VegetablePizzaProcess;

public class TemplateMethodPatternEx {
    public static void main(String ... args){
        new VegetablePizzaProcess().makePizza();
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        new NonVegetablePizzaProcess().makePizza();
    }
}
