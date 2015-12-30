package multithread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by leonlazuli on 2015-12-30.
 * 这个程序加
 */
public class MySpinLock {
    private AtomicBoolean locked = new AtomicBoolean();

    public MySpinLock(){
        locked.set(false);
    }

    public void lock(){
        while (!locked.compareAndSet(false,true));
    }

    public void unlock(){
        locked.set(false);
    }


    private static int value = 1000;
    private static Object objLock = new Object();
    private static MySpinLock lock = new MySpinLock();
    private static void foo(){
        lock.lock();
        try{
            value += 100;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            value -= 100;
            System.out.printf("%d %n", value);
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args){
        final int nThread = 10;
        for(int i = 0; i < nThread; i++){
            Thread t = new Thread(()->{
                while (true){
                    foo();
                }
            });
            t.start();
        }
    }



}
