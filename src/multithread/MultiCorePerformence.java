package multithread;

/**
 * Created by Leon on 2015/9/28.
 */

/*
���Խ����������Ԥ�ڣ� ���ĺ˻��ϣ�4��thread���ܹ��ﵽ��õ����ܡ� ��ÿ������һ���̣߳��ܵĹ����в��漰�߳��л���
*/
public class MultiCorePerformence {

    public static void main(String[] args){
        int nAdder = 4;
        long largestNumber = 1L << 31;
        long[] results = new long[nAdder];;
        long each = largestNumber / nAdder;
        Thread[] adders = new Thread[nAdder];

        long res = 0;
        long singleCoreStartTime = System.currentTimeMillis();
        for(long i = 0; i < largestNumber; i++){
            res += i;
        }
        System.out.printf("one thread use %d milliseconds, result is: %d %n", System.currentTimeMillis() - singleCoreStartTime, res);

        for(int i = 0; i < nAdder; i++){
            final int id = i;
            Thread adder = new Thread(()->{
                long start = id * each;
                long end = start + each;
                long result = 0;
                if(id == nAdder - 1)
                    end = largestNumber ;
                for(long k = start; k < end; k++){
                    result += k;
                }
                //System.out.printf("%d thread, start = %d, end = %d, result = %d%n", id, start, end, result);
                results[id] = result;
            });
            adders[i] = adder;
        }

        long multicoreStart = System.currentTimeMillis();
        for(Thread t:adders){
            t.start();
        }
        for(Thread t : adders){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long mulResult = 0;
        for(int i = 0; i < nAdder; i++){
            mulResult += results[i];
        }
        System.out.printf("multicore use %d milliseconds, result is: %d %n", System.currentTimeMillis() - multicoreStart, mulResult);

    }
}
