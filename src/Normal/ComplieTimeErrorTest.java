package normal;

import java.lang.*;
import java.util.*;

/**
 * Created by Leon on 2015/12/10.
 */

class Boo<T>{
    void foo(T value){
        List<T> list = new ArrayList<>();
        T a = list.get(0); // no CHECKCAST in byte code
    }

    void doo(){
        List<String> list = new ArrayList<>();
        String s = list.get(0);  // CHECKCAST java/lang/String  in byte code
    }
}

class Foo<T> implements Comparable<T>
{
    @Override
    public int compareTo(T t){
        return 1;
    }

}

class ExposeGenericArray<E>{
    public E[] array;   //SHOULD be priavte!!!!  Expose Generic Array to make runtime Error

    ExposeGenericArray(int length){
        array = (E[])new Object[length];
    }

    E get(int index){
        return array[index];
    }

    static void test(){
        ExposeGenericArray<String> arr = new ExposeGenericArray<>(10);
        Object[] objs = arr.array;
        objs[0] = "Hello";
        objs[1] = 33;

       System.out.println(arr.array[0]); // RunTimeError cast
       System.out.println(arr.array[1]);
        System.out.printf(arr.get(0));
        System.out.println(arr.get(1)); // RunTimeError cast
    }

}

public class ComplieTimeErrorTest {

    static <T> void foo(List<T> list){
        T[] snapshot = (T[])list.toArray();
        T value = snapshot[0];
    }

    public static void main(String[] args){
        ExposeGenericArray.test();
        foo(new ArrayList());
    }
}
