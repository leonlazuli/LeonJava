package multithread;

import javax.sound.midi.Soundbank;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by leonlazuli on 2015-12-28.
 * 主要是测同一个线程里对同一个锁的重入, 以及由于多个线程里由于指定了一个线程等待另一个线程结束(join,或者excutor)而造成的死锁.
 * 结论就是同步区域里不要调用外来方法.
 */
public class ReenterSynchronizedBlockTest {
    static Object lock = new Object();
    CopyOnWriteArrayList

    static void inner(){
        synchronized (lock){
            System.out.println("enter Inner");
            System.out.println("leave Inner");
        }
    }

    static void outter(){
        synchronized (lock){
            System.out.println("enter outter");
            inner();
            System.out.println("leave outter");
        }
    }

    static void outter_anotherThread(){
        synchronized (lock){
            Thread t = new Thread(()->{
                System.out.println("enter outter");
                inner();
                System.out.println("leave outter");
            });
            t.start(); //这里是没有问题的,因为主线程并没有等待另一个线程返回或者结束,所以主线程sleep结束之后会释放掉锁.
//            try { // 如果加上join的话,就可以死锁了,因为显示指定了主线程等待另一个线程结束.
//                t.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            // 用excutor也可以造成死锁,原因还是要等待excutor的get方法返回.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        outter_anotherThread();
    }

}
