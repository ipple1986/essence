package decorator.dp;

import decorator.dp.concrete.ConcreteComponent;
import decorator.dp.decorators.Decorator1Component;
import decorator.dp.decorators.Decorator2Component;
import decorator.dp.decorators.Decorator3Component;

public class DecoratorPatternEx {
    public static void main(String ...args){
       ConcreteComponent concreteComponent =  new ConcreteComponent();

       Decorator1Component decorator1Component  =  new Decorator1Component();
       decorator1Component.setComponent(concreteComponent);

        Decorator2Component decorator2Component = new Decorator2Component();
        decorator2Component.setComponent(decorator1Component);

        Decorator3Component decorator3Component = new Decorator3Component();
        decorator3Component.setComponent(decorator2Component);

        decorator3Component.doWork();
    }
}
