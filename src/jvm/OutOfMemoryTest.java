package jvm;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.*;



/**
 * Created by Leon on 2016/1/8.
 */

// -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+HeapDumpOnOutOfMemoryError
public class OutOfMemoryTest {
    static class SofTest
    {
        int depth = 0;
        void run(){
            depth++;
            run();
        }
    }

    static void heapOOM(){
        List<Object> list = new ArrayList<>();
        while(true){
            list.add(new Object());
        }
    }

    static void stackOverflow(){
        SofTest sof = new SofTest();
        try{
            sof.run();
        }catch (Exception e){
            System.out.printf("stack depth: %d %n",sof.depth);
            throw e;
        }
    }

    static void stackLeakByThread(){
        while (true){
            Thread t = new Thread(()->{
                while (true);
            });
            t.start();
        }
    }

    // ע����8.0���棬�ַ���������heap�У����������������Heap����
    static void runtimeConstantPoolOOM(){
        List<String> list = new ArrayList<>();
        int i = 0;
        while(true){
            list.add(String.valueOf(i++).intern());
        }
    }

    static void stringInternTest(){
        String s = "����ֵ�";
        String s1 = new StringBuilder("���").append("�ֵ�").toString();
        System.out.println(s1.intern() == s1);

        String s2 = new StringBuilder("ja").append("va").toString();
        System.out.println(s2.intern() == s2);
    }

    static  void directMemoryOOM(){
        Field unsafeFile = Unsafe.class.getDeclaredFields()[0];
        unsafeFile.setAccessible(true);
        try {
            Unsafe unsafe = (Unsafe)unsafeFile.get(null);
            while (true){
                unsafe.allocateMemory(1024*1024);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    static void catString(){
        String s = "hello";
        while (true){
            s += "aaaa";
        }
    }

    public static void main(String[] args){
        heapOOM();
    }

}
