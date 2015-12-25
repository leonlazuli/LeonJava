package multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Leon on 2015/9/28.
 */
public class VolatileTest {
     // volatile: write happens bofore read, 只保证写的值对后续的读操作立即可见，但不保证写对后续的写立即可见，所以不能拿来做互斥
    // java中读操作和写操作（除了long float）之外，都是保证原子性的，但是++这种操作涉及到读和写，就不能保证原子性了。
    private static volatile int nextCounter = 0;
    private static Random random = new Random();
    private static boolean stop = false;
    private static int max = 0;

    public static int getNextCounter(){
        return nextCounter++;  //nextCounter++ 不是原子操作， 先取值，再赋值，所以如果不加锁，不同线程的逻辑控制流就可能有穿插，就会错
    }



    public static void main(String[] args){
        final Thread main = Thread.currentThread();
        final int nThread = 5;
        final int nSlot = 100000000;
        List<Integer> duplicateCounters =  Collections.synchronizedList(new ArrayList<Integer>());
        Thread[] ts = new Thread[nThread];
        Boolean[] all = new Boolean[nSlot];

        for(int i = 0; i < nSlot; i++){
            all[i] = false;
        }

        for(int i = 0; i < nThread; i++){
            final int index = i;
            ts[i] = new Thread(()->{
                while(!stop) {
                    int temp = getNextCounter();
                    if(!all[temp]){
                        all[temp] = true;
                    } else {
                        duplicateCounters.add(temp);
                    }
                    if(temp > max)
                        max = temp;
                }
                System.out.printf("Thread%d end %n", Thread.currentThread().getId());
            });
            ts[i].start();
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main wake from sleep");
        stop = true;
        for(Thread t : ts){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("start analysing%n--------------------------------------%n");
        System.out.printf("max is %d %n", max);
        for(int i = 0; i < max; i++){
            if(!all[i])
                System.out.printf("missing %d %n", i);
        }
        for(Integer i : duplicateCounters){
            System.out.printf("Duplicate counter %s %n", i);
        }
        System.out.printf("main thread exit");

    }
}
