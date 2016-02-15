package multithread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Leon on 2016/1/20.
 * ��Ȼtask��������ջ�У���������Ȼ�������̹߳���ģ��������̷߳������˹����̣߳���
 * �������߳�Ҳͨ�����task���󣬰Ѽ��������ظ����̡߳� ʵ����ֻ��ͨ���������task
 * �������߳�����task�ڲ�״̬�����������߳�ȡtask�ڲ�״̬�������ѣ���Ȼ���м��漰��������
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

    // ע��getֻ��һ����ͨ���첽������get�׳��쳣��������FutureTask�����״̬���ƻ��ˣ�Ҫ���쳣�ľ������������
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
