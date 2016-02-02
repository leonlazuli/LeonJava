package multithread;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Leon on 2016/1/27.
 * 这里不会死锁，但是效率会很低。 因为最终会形成一条等待链，导致相当于整个程序都跑在一个锁上，吞吐量很低。
 */
public class DeadLock2 {

    static final AtomicLong counter = new AtomicLong(0);
    static final Random random = new Random();

    static class Foo{
        private final int id;

        public Foo(int id){
            this.id = id;
        }

        public int getId(){
            return id;
        }
    }

    public static void main(String[] args){
        final int nIter = 100000;
        final int nObj = 5;
        final int nThread = 20;


        final Foo[] objs = new Foo[nObj];

        for (int i = 0; i < nObj; i++){
            objs[i] = new Foo(i);
        }

        for(int i = 0; i < nThread; i++){
            new Thread(()->{
                for(int j = 0; j < nIter; j++){
                    trasfer(objs[random.nextInt(nObj)], objs[random.nextInt(nObj)]);
                }
            }).start();
        }

    }

    // 通过排序来避免死锁
    static void trasfer(Foo from, Foo to){
        Foo a,b;
        if(from.getId() < to.getId()) {
            a = from;
            b = to;
        }else{
            a = to;
            b = from;
        }
        System.out.printf("from %d to %d %n", a.getId(),b.getId());
        synchronized (a){
            synchronized (b){
                int i = Integer.MAX_VALUE / (random.nextInt(5) + 2) + 1;
                while (i++ > 0);
            }
        }
        long n = counter.addAndGet(1);
        System.out.println("finish " + n);
    }
}
