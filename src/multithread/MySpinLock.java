package multithread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by leonlazuli on 2015-11-30.
 * 这个加深了对 原子性 操作,和 critical section区别的理解. 原子性操作是保证操作不被打断,
 * 而critical section的保护也是通过加锁的方式来保证了其执行不会被打断,但是加互斥锁会导致其他尝试
 * 进入critical section的线程被阻塞(成为阻塞态), 而Atomic操作则是通过更底层的方式来保证这个cpu指令
 * 执行不会被打断. 所以Atomic不涉及将其他进程阻塞,这样效率更高.
 *
 * 这里SpinLock的用处是,在等待时间比较少,并且线程数量少于处理器数量的时候,用spinlock可以避免转换线程状态的开销
 * 从而提高效率.  但是如果等待时间长(多于转换线程状态的开销), 或者线程比处理器数量多,那么spinlock是不如用互斥锁.
 *
 * 这里实现的是一个不可重入的锁。 如果用AtomicInteger来记录ThreadID的话，可是实现一个可重入的。
 */
public class MySpinLock {
    private AtomicBoolean locked = new AtomicBoolean();

    public MySpinLock(){
        locked.set(false);
    }

    public void lock2(){
        while (!locked.compareAndSet(false,true));
    }

    // 这两种lock的写法都可以，这一种是标准写法，而上一种，因为boolean比较特殊，只有二值，所以可以简写，只是一种巧合，这一种更好理解一些。
    public void lock(){
        while (true){
            boolean isLocked = locked.get();
            if(isLocked)
                continue;
            else if(locked.compareAndSet(false,true))
                break;
        }
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
