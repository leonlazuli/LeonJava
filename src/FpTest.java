/**
 * Created by Leon on 2015/12/7.
 */

import java.lang.reflect.Array;
import java.util.*;

public class FpTest {
    public static void print(String s) {
        // System.out.println(s);
        Runnable r = () -> System.out.println("hello world");
        r.run();
    }

    public static void main(String[] args) {
        Integer[] array = {2, 5, 6, 7, 8, 3};
        List<Integer> list = Arrays.asList(array);
        //System.out.println();
        //Arrays.stream(list2).forEach(System.out::println);
        int result = list.stream().reduce((a,b)->a+b).get();
        System.out.println(result);


    }

}
