package multithread;

/**
 * Created by Leon on 2015/12/25.
 */
//@@TODO 写个不需要join的形式
public class DeadLock {
    private static final Object lock = new Object();
    static void deadLock(){
        synchronized (lock){
            Thread t = new Thread(()->{
               deadLock();
            });
            t.start();
            try {
                t.join(); // 这里主线程等待新线程结束，而新线程等待主线程释放lock，就形成了死锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final Object lock2 = new Object();

    // 线程1获得lock，等待lock2， 线程2获得lock2，等待lock 死锁。
    static void deadLock2(){
        Thread t1 = new Thread(()->{
            synchronized (lock){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2){

                }
            }
            System.out.println("t1 exit");
        });

        Thread t2 = new Thread(()->{
            synchronized (lock2){
                synchronized (lock){

                }
            }
            System.out.println("t2 exit");
        });
        t1.start();
        t2.start();
    }



    public static void main(String[] args){
        deadLock2();
        System.out.println("main exit");
    }
}
