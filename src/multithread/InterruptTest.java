package multithread;

import static java.lang.Thread.sleep;

/**
 * Created by Leon on 2016/1/20.
 * ����Interrupt������˵����Unix�Ľ����ź��е����ⲻ��������ͨ�������쳣�ķ�ʽ������
 * ����Interrupt��ʱ���Ǹ�����߳�������һ�����λ��ֻ�����̴߳���Blocked����Wait��ʱ�򣬲��ܹ��жϡ�
 */
public class InterruptTest {

    public static void goSleep() {
        while (true) {
            System.out.println("sleep once");
            int i = 1;
            while (i++ > 0); // ������Runnable��ʱ��᲻�����̴�ϣ������ǲ���

            try {
                sleep(500);
            } catch (InterruptedException e) {
                System.out.println("interrupted,but..., I still want sleep for another while.");
            }
        }
    }

    public static void testSleep()
    {
        Thread t1 = new Thread(()->{
            goSleep();
        });
        t1.start();
        t1.interrupt();
    }

    public static void testRecover(){
        Thread t = new Thread(()->{
           try{
               System.out.println("process something");
               sleep(500);
           } catch (InterruptedException e){
               System.out.println("catch interrput");
               Thread.currentThread().interrupt();
           }
        });
        t.start();
        t.interrupt();
    }

    public static void main(String[] args){
        testRecover();
    }
}
