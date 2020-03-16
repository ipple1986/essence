package adapter;

public class CalculatorAdapterExample {
    public static void main(String[] args) {
        Rectangle rectangle = new Rectangle();
        rectangle.width = 3;
        rectangle.height = 4;
        Calculator calculator = new Calculator();
        System.out.println("长方形面积:" + calculator.getArea(rectangle));
        System.out.println("==============================");

        CalculatorAdapter calculatorAdapter = new CalculatorAdapter();
        Triangle triangle = new Triangle();
        triangle.base = 4;
        triangle.height = 3;
        System.out.println("三角形面积:" + calculatorAdapter.getArea(triangle));
    }
}
class Rectangle{
     int width;
     int height;
}
class Triangle{
    int base;
    int height;
}
class Calculator{
    public int getArea(Rectangle rectangle){
        return rectangle.height *  rectangle.width;
    }
}
class CalculatorAdapter{
    public int getArea(Triangle triangle){
        Rectangle rectangle = new Rectangle();
        rectangle.height = triangle.height;
        rectangle.width = triangle.base / 2;
        Calculator calculator = new Calculator();
        return calculator.getArea(rectangle);
    }
}