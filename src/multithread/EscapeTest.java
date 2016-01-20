package multithread;

import java.lang.reflect.Constructor;
import java.util.AbstractCollection;

/**
 * Created by Leon on 2016/1/19.
 */
public class EscapeTest {
    String name = "java";
    public EscapeTest(Container c){
       c.register(new Foo(){
           @Override
            public void foo(){
               name = "super java"; // �����������ͨ���հ���ʽ����EscapeTest�з����˻�δ������ɵ�this��
               System.out.println(name);
           }
       });
    }

    public static void main(String[] args){
        Container c = new Container();
        Thread t = new Thread(()->{
            while(true) {
                EscapeTest e = new EscapeTest(c);
            }
        });
        t.start();
        while (true)
            c.trigger();

    }

}

abstract class Foo{
    public abstract void foo();
}

class Container{
    Foo f;
    void register(Foo f){
        this.f = f;
    }
    void trigger(){
        if(f != null)
            f.foo();
    }
}
