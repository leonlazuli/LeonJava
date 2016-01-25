package multithread;

import static java.lang.Thread.sleep;

/**
 * Created by Leon on 2016/1/20.
 * 测试Interrupt，简单来说，和Unix的进程信号有点像。这不过这里是通过捕获异常的方式来处理，
 * 调用Interrupt的时候，是给这个线程设置了一个标记位，只有在线程处于Blocked或者Wait的时候，才能够中断。
 */
public class InterruptTest {

    public static void goSleep() {
        while (true) {
            System.out.println("sleep once");
            int i = 1;
            while (i++ > 0); // 测试在Runnable的时候会不会立刻打断，结论是不会

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
