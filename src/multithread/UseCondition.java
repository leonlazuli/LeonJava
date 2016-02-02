package multithread;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Leon on 2016/2/1.
 * 用Lock和Condition来实现BlockingQueue，两个Condition对应两个Condition可以用signal而不需要用signalAll
 */
public class UseCondition {
    static class MyBlockingQueue<T>
    {
        final ReentrantLock lock = new ReentrantLock();
        final Condition empty = lock.newCondition();  // 这里用notEmpty 和 notFull，语义会更加符合conditionPredicate一点儿
        final Condition full = lock.newCondition();

        int capacity = 10;
        T[] buffer;
        int size;
        int front;
        int rear;

        {
            buffer = (T[])new Object[capacity];
            size = 0;
            front = 0;
            rear = 0;
        }

        public MyBlockingQueue(int capacity){
            this.capacity = capacity;
        }

        public MyBlockingQueue(){}

        public boolean isEmpty(){
            return size == 0;
        }

        public boolean isFull(){
            return size == capacity;
        }

        public void put(T v) throws InterruptedException {
            lock.lock();
            try{
                while (isFull()){
                    full.await();
                }
                buffer[rear] = v;
                size++;
                rear = (rear + 1) == capacity ? 0 : rear + 1;
                empty.signal(); // 可以优化，只在状态变化的时候signal
            }finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try{
                while (isEmpty())
                    empty.await();
                T ret = buffer[front];
                size--;
                front = front + 1 == capacity ? 0 : front + 1;
                full.signal();
                return ret;
            }finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args){
        ExecutorService exec = Executors.newCachedThreadPool();
        MyBlockingQueue<Integer> queue = new MyBlockingQueue<>();
        final int nConsumer = 10;
        final int nProducer = 10;
        CyclicBarrier barrier = new CyclicBarrier(nConsumer + nProducer);
        Random random = new Random();
        for(int i = 0; i < nConsumer; i++){
            final int id = i;
            exec.execute(()->{
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                while (true){
                    try {
                        int ret = queue.take();
                        System.out.printf("%d consume %d %n", id, ret);
                        Thread.sleep(random.nextInt(500));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        for(int i = 0; i <nProducer; i ++){
            final int id = i;
            exec.execute(()->{
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                while (true){
                    try {
                        int product = random.nextInt();
                        queue.put(product);
                        System.out.printf("%d produce %d %n", id, product);
                        Thread.sleep(random.nextInt(800));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }
}
