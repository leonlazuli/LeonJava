package multithread;

import java.time.temporal.Temporal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Leon on 2016/2/1.
 */
public class UseReentrantLock {
    static ReentrantLock lock = new ReentrantLock();

    static void testTryLock() {
        while (!lock.tryLock()) ; // 轮询，如果不成功就一直试
        try {
            System.out.println("got the lock");
        } finally {
            lock.unlock();
        }
    }

    static void testTryLockWithTimeOut()throws InterruptedException{
        if(!lock.tryLock(1000, TimeUnit.MILLISECONDS)){
            System.out.println("fail");
            return;
        }
        try {
            System.out.println("got lock");
        }finally {
            lock.unlock();
        }
    }

    static void testLockInterruptibly()throws InterruptedException{
        lock.lockInterruptibly();
        try{
            System.out.println("got lock");
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args){
        lock.lock();
        try {
            Thread t = new Thread(()->{
                try{
                    testLockInterruptibly();
                }catch (InterruptedException e){
                    System.out.println("interrupted");
                    Thread.currentThread().interrupt();
                }
            });
            t.start();
            int i = 1;
            while (i++ > 0);
            t.interrupt();
        } finally {
            lock.unlock();
        }
    }

}
