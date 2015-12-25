package multithread;

/**
 * Created by Leon on 2015/12/25.
 */
//@@TODO д������Ҫjoin����ʽ
public class DeadLock {
    private static final Object lock = new Object();
    static void deadLock(){
        synchronized (lock){
            Thread t = new Thread(()->{
               deadLock();
            });
            t.start();
            try {
                t.join(); // �������̵߳ȴ����߳̽����������̵߳ȴ����߳��ͷ�lock�����γ�������
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final Object lock2 = new Object();

    // �߳�1���lock���ȴ�lock2�� �߳�2���lock2���ȴ�lock ������
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
