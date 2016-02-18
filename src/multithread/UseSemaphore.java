package multithread;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by Leon on 2016/1/20.
 * 一个简单的Producer Consumer，用Semaphore实现。写法和Unix中的一样，但是Semaphore底层实现不同。
 * 没有实际的用到buffer，用到的话和Unix中一样，还是要加mutex。
 */
public class UseSemaphore {
    final static int capacity = 5;
    final static Semaphore empty = new Semaphore(capacity);
    final static Semaphore full = new Semaphore(0);
    final static int nProducer = 10;
    final static int nConsumer = 10;
    final static Random random = new Random();


    public static void main(String[] args){
        for(int i = 0; i < nProducer; i++){
            final int id = i;
            new Thread(()->{
                while(true) {
                    try {
                        empty.acquire();
                        System.out.printf("producer %d produce a product %n", id);
                        full.release();
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for(int i = 0; i < nConsumer; i++){
            final int id = i;
            new Thread(()->{
                while (true){
                    try {
                        full.acquire();
                        System.out.printf("consumer %d consume a product %n", id);
                        empty.release();
                        Thread.sleep(random.nextInt(100));
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
