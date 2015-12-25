package multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Leon on 2015/9/28.
 */
public class VolatileTest {
     // volatile: write happens bofore read, ֻ��֤д��ֵ�Ժ����Ķ����������ɼ���������֤д�Ժ�����д�����ɼ������Բ�������������
    // java�ж�������д����������long float��֮�⣬���Ǳ�֤ԭ���Եģ�����++���ֲ����漰������д���Ͳ��ܱ�֤ԭ�����ˡ�
    private static volatile int nextCounter = 0;
    private static Random random = new Random();
    private static boolean stop = false;
    private static int max = 0;

    public static int getNextCounter(){
        return nextCounter++;  //nextCounter++ ����ԭ�Ӳ����� ��ȡֵ���ٸ�ֵ�������������������ͬ�̵߳��߼��������Ϳ����д��壬�ͻ��
    }



    public static void main(String[] args){
        final Thread main = Thread.currentThread();
        final int nThread = 5;
        final int nSlot = 100000000;
        List<Integer> duplicateCounters =  Collections.synchronizedList(new ArrayList<Integer>());
        Thread[] ts = new Thread[nThread];
        Boolean[] all = new Boolean[nSlot];

        for(int i = 0; i < nSlot; i++){
            all[i] = false;
        }

        for(int i = 0; i < nThread; i++){
            final int index = i;
            ts[i] = new Thread(()->{
                while(!stop) {
                    int temp = getNextCounter();
                    if(!all[temp]){
                        all[temp] = true;
                    } else {
                        duplicateCounters.add(temp);
                    }
                    if(temp > max)
                        max = temp;
                }
                System.out.printf("Thread%d end %n", Thread.currentThread().getId());
            });
            ts[i].start();
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main wake from sleep");
        stop = true;
        for(Thread t : ts){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("start analysing%n--------------------------------------%n");
        System.out.printf("max is %d %n", max);
        for(int i = 0; i < max; i++){
            if(!all[i])
                System.out.printf("missing %d %n", i);
        }
        for(Integer i : duplicateCounters){
            System.out.printf("Duplicate counter %s %n", i);
        }
        System.out.printf("main thread exit");

    }
}
