package multithread;


import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by Leon on 2016/1/21.
 * 测试CyclicBarrier， 正如其名，可重用的栅栏。 其实可以用CountDownLatch来模拟。不过这个更简单，而且
 * 创建CyclicBarrier的时候可以传一个所有线程到达栅栏的时候回调的函数对象。这个比较好用。
 */
public class UseCyclicBarrier {
    private final CyclicBarrier barrier;
    private final Worker[] workers;
    private final int[] body;
    private Random random = new Random();

    public UseCyclicBarrier(){
        int count = Runtime.getRuntime().availableProcessors();
        barrier = new CyclicBarrier(count, ()->{report();});
        workers = new Worker[count];
        for(int i = 0; i < count; i++){
            workers[i] = new Worker(i);
        }
        body = new int[count];
    }

    public void start(){
        for(Worker w : workers){
            new Thread(w).start();
        }
    }

    private void report(){
        System.out.println("component is: ");
        String comma = "";
        long total = 0;
        for(int compoent : body){
            total += compoent;
            System.out.printf(comma + "%d", compoent);
            comma = ", ";
        }
        System.out.printf("%n total is : %d", total);
    }

    private class Worker implements Runnable{
        private int id;

        Worker(int id){
            this.id = id;
        }

        @Override
        public void run(){
            body[id] = random.nextInt();
            try {
                barrier.await();
            } catch (InterruptedException e) {
                return;
            } catch (BrokenBarrierException e) {
                return;
            }
        }
    }

    public static void main(String[] args){
        UseCyclicBarrier u = new UseCyclicBarrier();
        u.start();
    }
}
