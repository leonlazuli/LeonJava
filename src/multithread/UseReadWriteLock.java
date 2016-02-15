package multithread;

import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Leon on 2016/2/1.
 */
public class UseReadWriteLock {
    static ReadWriteLock rwlock = new ReentrantReadWriteLock();
    static Lock readLock = rwlock.readLock();
    static Lock writeLock = rwlock.writeLock();
    static int data = 1;

    public static void main(String[] args) throws InterruptedException{
        Runnable r = ()->{
            while (true) {
                readLock.lock();
                try {
                    System.out.println("reader got lock");
                    Thread.sleep(1000);
                    System.out.println(data);

                } catch (InterruptedException e) {
                    System.out.println("interrupted");
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println("reader release lock");
                    readLock.unlock();
                    break;
                }
            }
        };
        Thread rt1 = new Thread(r);
        Thread rt2 = new Thread(r);
        Thread wt = new Thread(()->{
            writeLock.lock();
            try{
                System.out.println("writer got lock");
                Thread.sleep(1000);
                data = 33;
            } catch (InterruptedException e) {
                System.out.println("interrupted");
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("writer release lock");
                writeLock.unlock();
            }
        });
        wt.start();
        Thread.sleep(2);
        rt1.start();
    }
}
