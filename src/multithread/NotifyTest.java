package multithread;

import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 2015/12/28.
 */
public class NotifyTest
{
    private final static Object lock = new Object();
    public static void main(String[] args){
        final int nThread = 2;
        Thread[] ts = new Thread[nThread];
        for(int i = 0; i < nThread; i++) {
            final int id = i;
            Thread t = new Thread(() -> {
                System.out.printf("Thread %d wait for lock %n", id);
                synchronized (lock) {
                    System.out.printf("Thread %d get lock %n", id);
                    try {
                        lock.wait();
                        System.out.printf("Thread %d awake %n", id);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.printf("Thread %d exit %n", id);
            });
            t.start();
        }

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread tNofity = new Thread(()->{
            synchronized (lock){
                lock.notifyAll();
            }
        });
        tNofity.start();
    }
}
