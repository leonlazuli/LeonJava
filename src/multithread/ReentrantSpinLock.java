package multithread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leonlazuli on 2016-02-14.
 */
public class ReentrantSpinLock {
    public static final int AVAILABLE = -1;
    final AtomicLong owner = new AtomicLong(); // -1 means available
    int amount = 0;

    public ReentrantSpinLock(){
        owner.set(AVAILABLE);
    }

    public void lock(){
        while (true){
            if(owner.get() != Thread.currentThread().getId()){
                if(owner.compareAndSet(AVAILABLE, Thread.currentThread().getId())){
                    amount = 1;
                    break;
                }
            }else{ // 能够确定是一个线程中,不存在竞争
                amount += 1;
            }
        }
    }

    public void unlock(){
        if(owner.get() == Thread.currentThread().getId()){
            amount--;
            //这里应该不用,因为if为真的时候,其他线程不可能获得这个锁.
            if(amount == 0)
                owner.compareAndSet(Thread.currentThread().getId(),AVAILABLE);
        }
    }

    static void add(int times)
    {
        counter++;
        if(times != 0)
            add(times - 1);
    }
    public static int counter = 0;
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        final int n = 10;
        final CountDownLatch latch = new CountDownLatch(10);
        final ReentrantSpinLock lock = new ReentrantSpinLock();
        for(int i = 0; i < 10; i++){
            executor.execute(()->{
                for(int j = 0 ; j < 1000; j++){
                    lock.lock();
                    try{
                        add(3);
                    }finally {
                        lock.unlock();
                    }
                }
                latch.countDown();
            });
        }
        latch.await();
        System.out.println(counter);
        executor.shutdown();
        executor.awaitTermination(0l, TimeUnit.MICROSECONDS);
    }




}
