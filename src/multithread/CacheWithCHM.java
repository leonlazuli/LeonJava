package multithread;

import normal.Main;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Leon on 2016/1/21.
 * 这里注意两个事情， test-set的过程用CHM的putIfAbsent来解决。 然后注意出现异常的时候，要把污染的缓存清掉。
 */
public class CacheWithCHM {
    // A 参数类型， V返回值类型
    interface IComputerable<A,V>{
        V compute(A arg) throws InterruptedException;
    }

    static class ExpensiveFunction implements IComputerable<String, BigInteger>{
        @Override
        public BigInteger compute(String arg){
            // 模拟长时间计算
            int i = 0;
            while (i++ > 0);

            return new BigInteger(arg);
        }
    }

    static class Memoizer<A,V> implements IComputerable<A,V>{
        private final IComputerable<A,V> originalComputerable;
        private final Map<A,Future<V>> cache = new ConcurrentHashMap<A,Future<V>>();

        public Memoizer(IComputerable<A,V> c){
            this.originalComputerable = c;
        }

        @Override
        public V compute(A arg) throws InterruptedException{
            while (true) { // ## 注意这里会一直试到成功
                Future<V> f = cache.get(arg);
                if (f == null) {
                    FutureTask<V> ft = new FutureTask<V>(() -> {
                        return originalComputerable.compute(arg);
                    });
                    f = cache.putIfAbsent(arg, ft);
                    if (f == null) {
                        f = ft;
                        System.out.printf("compute for %s %n at %d &n", arg.toString(), System.currentTimeMillis());
                        ft.run();
                    }
                }
                try {
                    System.out.printf("%d try get %s result at %d %n", Thread.currentThread().getId(), arg.toString(), System.currentTimeMillis());
                    V ret = f.get();
                    System.out.printf("%d got %s result: %s at %d %n", Thread.currentThread().getId(), arg.toString(), ret.toString(), System.currentTimeMillis());

                    return f.get();
                } catch (CancellationException e) {
                    cache.remove(arg, f);
                } catch (InterruptedException e) {
                    throw e;
                } catch (ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }

            }
        }
    }

    public static void main(String[] args){
        String arg = "333333333333";
        Memoizer<String,BigInteger> memoizer = new Memoizer<>(new ExpensiveFunction());
        for(int i = 0; i < 10; i++){
            Thread t = new Thread(()->{
                try {
                    memoizer.compute(arg);
                } catch (InterruptedException e) {
                    //throw new RuntimeException(e);
                }
            });
            t.start();
            if(i == 6)
                t.interrupt();

        }
    }


}
