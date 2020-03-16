package decorator;

import decorator.Component;

public abstract  class AbstractDecoratorComponent implements Component {
    Component component;
    @Override
    public void doWork() {
        if(component!=null){
            component.doWork();
        }
    }
    public void setComponent(Component component){
        this.component = component;
    }

}
