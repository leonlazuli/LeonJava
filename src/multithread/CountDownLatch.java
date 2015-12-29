package multithread;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Created by Leon on 2015/10/29.
 * 模拟counterDownLatch，用简单的wait notify就行。 注意用私有锁的时候，记得wait和notify也是在这个锁上。
 * 巩固了对一个抽象的理解，即每个一个thread的主体是一个栈，thread跑的时候，栈的状态随之变化，而thread被
 * block的时候，栈的状态暂时保持。
 */
public class CountDownLatch {
    private int count;
    private final Object lock = new Object();
    public CountDownLatch(int init){
        if(init <= 0){
            throw new IllegalArgumentException("count must be positive ");
        }
        this.count = init;
    }

    public void await(){
        synchronized (lock){
            while(count != 0)
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    public void countDown(){
        synchronized (lock){
            count--;
            if(count == 0);
                lock.notifyAll();
        }
    }

    public static void main(String[] args){
        final int nThread  = 3;
        Thread[] threads = new Thread[nThread];
        CountDownLatch ready = new CountDownLatch(nThread);
        CountDownLatch start = new CountDownLatch(1);

        for(int i = 0; i < nThread; i++){
            final int id = i;
            Thread t = new Thread(()->{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%d ready%n",id);
                ready.countDown();
                start.await();
                System.out.printf("%d start %n", id);
            });
            t.start();
        }
        System.out.printf("main await for ready %n");
        ready.await();
        start.countDown();
        System.out.println("main exit");
    }

}
