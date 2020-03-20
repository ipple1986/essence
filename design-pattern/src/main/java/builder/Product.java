package builder;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private List<String> parts = new ArrayList<>();
    public void add(String s){
        this.parts.add(s);
    }
    public void show(){
        System.out.println("================");
        for(String p:parts){
            System.out.println(p);
        }
    }
}
