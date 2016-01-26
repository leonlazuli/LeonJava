package multithread;

import java.util.TreeMap;
import java.util.concurrent.*;

/**
 * Created by leonlazuli on 2016-01-25.
 *  测试用newSingleThreadPoolExecutor的死锁问题
 */
public class UseSingleExcutorDeadLock {
    static ExecutorService exec = Executors.newSingleThreadExecutor();
    public static void main(String[] args) throws InterruptedException {
        Callable<String> r = ()->{
            int i = 1;
            while(i++ > 0);
            return "hello";
        };

        Future<String> f1;

//        f1 = exec.submit(r);  // 这里没有死锁,看看书上的例子是为啥死锁了,书上是因为提交f1 f2的本身也是个任务
//        f2 = exec.submit(r);

        f1 = exec.submit(()->{
            Future<String> f2 = exec.submit(()->{return "hello";}); //这里因为f2要等待f1结束才能分配到线程执行,而f1却在等待f2的结果,所以死锁.
            return f2.get();
        });

        String s="";
        try {
            s = f1.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.getCause().printStackTrace();
        }finally {
            exec.shutdown();
            exec.awaitTermination(1000,TimeUnit.MILLISECONDS);
            System.out.println("main exit " + s);
        }

    }

}
