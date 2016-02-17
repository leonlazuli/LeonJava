package multithread;

import java.util.concurrent.*;

/**
 * Created by Leon on 2016/1/26.
 */
public class UseFutureTask2 {

    static class MyFutureTask<V> extends FutureTask<V> {

        public MyFutureTask(Runnable r,V ret) {
            super(r,ret);
        }

        public MyFutureTask(Callable<V> callable){
            super(callable);
        }

        @Override
        public void done(){
            System.out.printf("done is called in thread: %d %n", Thread.currentThread().getId());
        }
    }

    static class MyExecutor extends ThreadPoolExecutor{
        public MyExecutor() {
            super(1,1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        }

        @Override
        protected <T> FutureTask<T> newTaskFor(Callable<T> callable){
           return new MyFutureTask<T>(callable);
        }

        @Override
        protected <T> FutureTask<T> newTaskFor(Runnable runnable, T result){
            return new MyFutureTask<T>(runnable, result);
        }

    }

    public static void main(String[] args){
        ExecutorService exec = new MyExecutor();
        System.out.println("main thread is : " + Thread.currentThread().getId());
        try {
            exec.submit(() -> {
                System.out.printf("task is run in: %d %n", Thread.currentThread().getId());
            });
        }finally {
            exec.shutdown();
        }
    }

}
