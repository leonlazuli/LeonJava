package multithread;

import sun.java2d.loops.GraphicsPrimitive;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by leonlazuli on 2015-09-26.
 */
public class producerConsumer {

    interface IProductQueue{
        void put(String product);
        String get();
    }

    private static class ProductQueue implements IProductQueue
    {
        int n;
        int front;
        int rear;
        final int capacity;
        String[] buffer;

        public ProductQueue(int capacity){
            if(capacity < 1){
                throw new IllegalArgumentException("capacity must be non-negative");
            }
            this.capacity = capacity;
            this.n = 0;
            this.buffer = new String[capacity];
            this.front = 0;
            this.rear = 0;
        }

        public synchronized void put(String product){
            /* 这里,wait暂时释放this的锁,挂起,等待被唤醒. 而唤醒之后,如果n依然==capacity的话,会继续挂起.
             所以真正"继续执行"的条件是n!=capacity. 也就是说,producer是可能被另一个producer的notifyfAll()唤醒,
             但是检测while loop 条件不满足之后又挂起了.这里 n == capacity 和 n == 0, 和C semaphore中的empty full有点像,
             但是不同的是, V(empty)只会唤醒empty挂起的线程, V(full)只会唤醒full的, 而java中, this.notifyAll 既可以唤醒get 也可以
             唤醒put, 只不过wait外部的full循环保证了如果"继续的条件"不满足,就会继续挂起. 所以,这里可能要用notifyAll,因为很有
             可能Notify的东西又继续挂起了,所以要用NofifyAll找到一个真正满足了"继续执行"条件的线程.
             semaphore不需要外部的while loop, 是因为它对于empty 和 full分别有两个,所以V(s)操作一定会唤醒一个合适的线程,而java里只用到了
             一个object的lock,所以需要在外部手工确保唤醒的合适的线程(不合适就继续休眠)

             注意,这里如果用模拟两个semaphore的话,也是要先获得semaphore, 再加锁,而不是在function上写synchronized(相当于给这个函数加锁),
             否则会出现同样的, 获得了锁的线程被semaphore阻塞,导致所有线程都无法继续.
            */
            while (n == capacity) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            buffer[rear] = product;
            rear = round(rear);
            ++n;
            System.out.printf("%d %n", n);

            /*
            * 这里一定要用NotifyAll()的原因就在于,唤醒的不一定是"能继续执行"的线程,可能唤醒之后就立刻继续休眠了.比如被get唤醒的是另一个get,那么这个while检测到
            * 继续的条件不满足之后,又会继续挂起. ("这里NotifyAll是一个native方法,应该是某种系统调用,所以应该不存在被NotifyAll唤醒的线程继续调用NotifyAll导致
            * 栈溢出的情况)
            */
            notifyAll();
        }

        public synchronized String get(){
            while (n == 0){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String ret = buffer[front];
            front = round(front);
            --n;
            System.out.printf("%d %n", n);
            notifyAll();
            return ret;
        }

        private int round(int i){
           return i % capacity;
        }
    }

    //------
    /*
       这里用两个lock来模拟semaphore.
       !!! 实践证明这里并不能这样做,是因为notify必须是在已经获得了这个锁的基础上. 而producer consumer中, notify的是另一个锁,所以不能用这种方式来模拟.
       虽然可以在notify的时候先获得另一个锁再notify, 两种锁需要操作同一个变量(比如代表这里代表buffer大小的n), 对n的操作必须是原子性的,所以需要另一个锁在做这件事情,
       但是这样又会出现同样的问题,先require了锁,然后require empty(或者full), 然后empty.wait(),
    */
    private static class ProductQueue_Semaphore implements IProductQueue
    {
        int n;
        int front;
        int rear;
        final int capacity;
        final Object mutex = new Object();
        final static Object semaphoreEmpty = new Object();  //TODO  如果确认不是this的问题,还是把这两个东西放回去.
        final static Object semaphoreFull = new Object();
        Lock lock = new ReentrantLock();
        int nForSemaphore = 0;
        String[] buffer;

        public ProductQueue_Semaphore(int capacity){
            if(capacity < 1){
                throw new IllegalArgumentException("capacity must be non-negative");
            }
            this.capacity = capacity;
            this.n = 0;
            this.buffer = new String[capacity];
            this.front = 0;
            this.rear = 0;
        }

        public void put(String product) {
            lock.lock();
            try {
                if(nForSemaphore == capacity) {
                    synchronized (semaphoreFull) {
                        try {
                            lock.unlock();
                            semaphoreFull.wait();
                            lock.lock();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                nForSemaphore++;
            }finally {
                lock.unlock();
            }

            synchronized (this) {
                buffer[rear] = product;
                rear = round(rear);
                ++n;
                System.out.printf("%d %n", n);
            }

            synchronized (semaphoreEmpty) {
                semaphoreEmpty.notify(); // 这里不能这样用,因为并没有获得empty的锁,所以也不能notify.
            }
        }

        public String get(){
            lock.lock();
            try{
                if(nForSemaphore == 0)
                    synchronized (semaphoreEmpty) {
                        try {
                            lock.unlock();
                            semaphoreEmpty.wait();
                            lock.lock();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                nForSemaphore--;
            }finally {
                lock.unlock();
            }
            String ret;
            synchronized (this){
                ret = buffer[front];
                front = round(front);
                --n;
                System.out.printf("%d %n", n);
            }
            synchronized (semaphoreFull) {
                semaphoreFull.notify();
            }
            return ret;
        }

        private int round(int i){
            return i % capacity;
        }
    }
    //------


    private static IProductQueue queue = new ProductQueue_Semaphore(1);
    private static Random random = new Random();

    public static void main(String[] args){
        final int nProducer = 10;
        final int nComsumer = 10;

        for(int i = 0; i < nProducer; i++){
            final int id = i;
            Thread tp = new Thread(()->{
                    while (true){
                        String product = String.format("%f",random.nextFloat());
                        queue.put(product);
                        System.out.printf("producer %d produce product: %s %n", id, product);
                        try {
                            TimeUnit.MILLISECONDS.sleep(random.nextInt(nComsumer) + nComsumer);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            tp.start();
        }

        for(int i = 0; i < nComsumer; i++){
            final int id = i;
            Thread tc = new Thread(()->{
                    while(true) {
                        String product = queue.get();
                        System.out.printf("comsumer %d consume product: %s %n", id, product);
                        try {
                            TimeUnit.MILLISECONDS.sleep(random.nextInt(nProducer) + nProducer);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            tc.start();
        }


    }


}
