package multithread;

/**
 * Created by Leon on 2015/12/30.
 */
public class VolatileWhileTest
{
    static volatile boolean end = false;

    public static void main(String[] args){
        Thread t  = new Thread(()->{
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            end = true;
        });
        t.start();
        while (!end);
        System.out.println("main exit");;
    }
}
