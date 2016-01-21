package multithread;

/**
 * Created by Leon on 2016/1/20.
 */
public class CountDownLatchImplement {
    private final Object lock = new Object();
    private int counter;

    public CountDownLatchImplement(int init){
        this.counter = init;
    }

    public void await() throws InterruptedException {
        synchronized (lock) {
            while (counter > 0) {
                lock.wait();
            }
        }
    }

    public void countDown(){
        synchronized (lock){
            counter--;
            if(counter <= 0)
                lock.notifyAll();
        }
    }

    public static void main(String[] args) throws InterruptedException{
        int count = 5;
        CountDownLatchImplement latch = new CountDownLatchImplement(count);
        Thread t = new Thread(()->{
            System.out.println("fire wait...");
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Fire!!! BOOM!");
        });
        t.start();

        Thread.sleep(100);
        while (count > 0){
            System.out.println(count);
            Thread.sleep(200);
            count--;
            latch.countDown();
        }

    }


}
