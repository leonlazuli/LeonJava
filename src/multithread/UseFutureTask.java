package multithread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Leon on 2016/1/20.
 * 虽然task的引用在栈中，但是它仍然是两个线程共享的（它被主线程发布给了工作线程）。
 * 而工作线程也通过这个task对象，把计算结果返回给主线程。 实质上只是通过共享对象task
 * ，工作线程设置task内部状态变量，而主线程取task内部状态变量而已，当然，中间涉及了阻塞。
 */
public class UseFutureTask
{
    static void test() throws ExecutionException, InterruptedException {
        FutureTask<Long> task =
                new FutureTask<Long>(() -> {
                    long result = 0L;
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        result += i;
                    }
                    return result;
                });
        Thread t = new Thread(task);
        t.start();
        System.out.println("try get result");
        long result = task.get();
        System.out.println(result);
    }

    // 注意get只是一个普通的异步方法，get抛出异常并不代表FutureTask本身的状态被破坏了，要视异常的具体情况而定。
    static void testTimeOut(){
        FutureTask<Long> task = new FutureTask<Long>(()->{
            int i = 1;
            while(i++ > 0);
            return 33L;
        });
        new Thread(task).start();
        try {
            task.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("time out");
        }

        try {
            long result = task.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testTimeOut();
    }
}
