package multithread;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Created by Leon on 2015/10/29.
 * ģ��counterDownLatch���ü򵥵�wait notify���С� ע����˽������ʱ�򣬼ǵ�wait��notifyҲ����������ϡ�
 * �����˶�һ���������⣬��ÿ��һ��thread��������һ��ջ��thread�ܵ�ʱ��ջ��״̬��֮�仯����thread��
 * block��ʱ��ջ��״̬��ʱ���֡�
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
