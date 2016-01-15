package jvm;

import sun.misc.Unsafe;

import java.util.Vector;

/**
 * Created by Leon on 2016/1/13.
 * 查看一些JIT的编译信息
 */
public class ViewJITDetail {
    public static final int NUM = 30000;

    public static int doubleValue(int i){
        for(int j = 0; j < 10000; j++);
        return i *= 2;
    }

    public static long calcSum(){
        long sum = 0;
        for(int i = 0; i <= 100; i++){
            sum += doubleValue(i);
        }
        return sum;
    }

    public static void main(String[] args){
        for(int i = 0; i < NUM; i++){
            calcSum();
        }
    }
}
