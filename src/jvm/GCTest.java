package jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2016/1/11.
 */
public class GCTest {
    private static final int _1MB = 1024 * 1024;

    public static void testPromotion(){
        byte[] a1,a2,a3,a4;
        a1 = new byte[_1MB / 4];
        a2 = new byte[4 * _1MB];
        a3 = new byte[4 * _1MB];
        a3 = null;
        a3 = new byte[4 * _1MB];
    }

    public static void testTenuringThreshold(){

    }

    public static void main(String[] args){
        testPromotion();
    }

}
