package normal;

import java.util.Collections;

/**
 * Created by Leon on 2016/1/19.
 */
public class SimpleTest {
    public static void main(String[] args){
        Container c = new Container();
        String[] arr = c.publish();
        arr[2] = "super java";
        c.dump();
    }
}

class PublishTest{
    private Container ctn = new Container();
}

class Container{
    private String[] arr = {"hello", "world", "java"};
    private String name = "hello";
    public String[] publish(){
        return arr;
    }

    public void dump(){
        for(String s : arr){
            System.out.println(s);
        }
    }
}
