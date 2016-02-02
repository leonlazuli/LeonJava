package multithread;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * Created by Leon on 2016/1/22.
 * 一个简单的ThreadPool Excutor的概念实现，不考虑中断和伸缩
 */
public class MyThreadPoolExcutor implements Executor {
    private final int nThread;
    private boolean started = false;
    private final BlockingQueue<Runnable> queue;
    private static float FACTOR = 2;

    public MyThreadPoolExcutor(int nThread){
        this.nThread = nThread;
        queue = new ArrayBlockingQueue<>(Math.round(nThread * FACTOR));
    }

    public int getNWaitingTask (){
        return queue.size();
    }

    public void start(){
        for(int i = 0; i < nThread ;i ++){
            new Thread(()->{
                while (true){
                    try {
                        Runnable r = queue.take();
                        r.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                }
            }).start();
        }
    }

    @Override
    public void execute(Runnable command) {
        if(!started){
            start();
            started = true;
        }

        try {
            queue.put(command);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args){
        MyThreadPoolExcutor me = new MyThreadPoolExcutor(10); // 只是为了测试queue大小才用实际类型引用它，正常使用的时候用Excutor接口引用它就可以了。
        Executor e = me;
        Random random = new Random();
        long counter = 0;
        while (true){
            counter++;
            long i = counter;
            System.out.printf("size of queue is %d %n",me.getNWaitingTask());
            e.execute(()->{
                int j = Math.abs(random.nextInt()) + 1;
                while (j > 0) j++; //模拟处理task消耗的时间

                System.out.printf("%d process task %d %n", Thread.currentThread().getId(), i);
            });
        }
    }
}
