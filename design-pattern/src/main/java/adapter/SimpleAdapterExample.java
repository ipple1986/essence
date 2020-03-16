package adapter;

public class SimpleAdapterExample {
    public static void main(String[] args) {
        ClassAdapter classAdapter = new ClassAdapter();
        ObjectAdapter objectAdapter =  new ObjectAdapter(new IntegerValue());
    }
}
interface  IIntegerValue{
    int getInteger();
}
class IntegerValue implements IIntegerValue{

    @Override
    public int getInteger() {
        return 2;
    }
}
class ClassAdapter extends IntegerValue{

    @Override
    public int getInteger() {
        return 2+ super.getInteger();
    }
}

class ObjectAdapter {
    IIntegerValue iIntegerValue;
    public ObjectAdapter(IIntegerValue iIntegerValue){
        this.iIntegerValue = iIntegerValue;
    }
    public int getInteger() {
        return 2+ this.iIntegerValue.getInteger();
    }
}