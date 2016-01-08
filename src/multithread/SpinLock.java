package multithread;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Leon on 2015/12/30.
 */
public class SpinLock {
    private volatile boolean locked = false;

    public void lock(){
        while (locked) ; // 不行，因为下面设置false的时候，这里可能有多个等待中的线程进入，因为没有锁locked @@尝试用automaticReference那个
        synchronized (this) {
            //synchronized (this) {
                locked = true;
            //}
        }
    }

    public void unlock(){
        synchronized (this) {
            locked = false;
        }
    }

    public static void testOne() {
        SpinLock lock = new SpinLock();
        Thread t = new Thread(()->{
            System.out.println("pair thread start");
            lock.lock();
            try{
                System.out.println("pair thread get the lock");
            }finally {
                lock.unlock();
            }
        });

        lock.lock();
        try {
            t.start();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
            System.out.println("main thread unlock");
        }
    }

    private static int value = 1000;
    private static SpinLock lock = new SpinLock();
    public static Object objLock = new Object();
    private static void foo() throws InterruptedException {
        value += 100;
        Thread.sleep(100);
        value -= 100;
        System.out.printf("%d %n", value);
    }

    public static void testTwo(){
        final int nThread = 10;
        for(int i = 0; i < nThread; i++){
            new Thread(()->{
                while (true) {
                    synchronized (objLock) {
                        try {
                            foo();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }).start();
        }
    }

    public static void main(String[] args){
        testTwo();
    }
}
