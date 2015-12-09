import java.sql.BatchUpdateException;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Account {
    private Byte[] lock1 = new Byte[0];
    private Byte[] lock2 = new Byte[0];
    private Integer value;
    private List<Integer> list = new ArrayList<>();

    public Account(int initValue) {
        this.value = initValue;
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
    }

    void infinitePrint(int milliInterval, String content) {
        long lastTime = System.currentTimeMillis();
        while (true) {
            value += 100;
            if (System.currentTimeMillis() - lastTime > milliInterval) {
                lastTime = System.currentTimeMillis();
                System.out.println(content + lastTime);
                System.out.println(value);
            }
            value -= 100;

        }
    }

    public void deposit(int amount) {
        synchronized (lock1) {
            infinitePrint(500, "deposite");
        }
    }

    public void withDraw(int amount) {
        {
            infinitePrint(500, "withDraw");
        }
    }

    public void recursive() {
        synchronized (list) {
            list.remove(1);
            for (Integer i : list) {
                recursive();
            }

        }
    }

    public void PrintValue() {
        System.out.println(value);
    }

    public int getValue() {
        return value;
    }
}

interface SetObserver<E> {
    void added(ObservableSet<E> set, E element);
}

class ObservableSet<E> extends HashSet<E> {
    ObservableSet(Set<E> set) {
        super(set);
    }

    private final List<SetObserver<E>> observers = new ArrayList<>();

    void addObserver(SetObserver<E> observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    void removeObserver(SetObserver<E> observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    void notifyElementAdded(E element) {
        List<SetObserver<E>> snapshot = null;
        synchronized (observers) {
            snapshot = new ArrayList<>(observers);
        }
        for (SetObserver<E> e : snapshot) // 遍历副本的同时删除了原来的对象中的一个entry，这个是没有问题的。
        {
            e.added(this, element);
        }
    }


    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (added) {
            notifyElementAdded(element);
        }
        return added;
    }

}

class Boo {
    Object lock = new Object();

    void doWait()
    {
        while (true) {
            synchronized (lock) {
                long tid = Thread.currentThread().getId();
                System.out.printf("%d wait%n", tid);
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%d end%n", tid);
            }
        }
    }

    void doNotifyAll()
    {
        synchronized (lock){
            lock.notify();
            System.out.printf("%d release the lock%n", Thread.currentThread().getId());
        }

    }
}


class Custom {
    private final String name;
    private final long id;
    public int value = 5000;

    Custom(Custom c) {
        this(c.name, c.id);
    }

    Custom(String name, long id) {
        this.name = name;
        this.id = id;
    }
}

class LeonThread extends Thread {
    final private Custom custom;

    LeonThread(Custom custom) {
        //super(run);
        this.custom = new Custom(custom);
    }

    public void run() {
        changeValue(100);
    }

    void changeValue(int magtitude) {
        synchronized (custom) {
            final long interval = 500; // milli
            long nextTime = System.currentTimeMillis();
            while (true) {
                //if(System.currentTimeMillis() > nextTime)
                {
                    //nextTime = System.currentTimeMillis() + interval;
                    custom.value += magtitude;
                    System.out.println(custom.value);
//                try {
//                    sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                    custom.value -= magtitude;
                    System.out.println(custom.value);
                }
            }
        }
    }
}

class Buffer {
    public final int max;
    int buffer = 0;

    Buffer(int max) {
        this.max = max;
    }

    int getBuffer() {
        return buffer;
    }

    void putToBuffer() {
        buffer += 1;
        System.out.println(buffer);
    }

    void getFromBuffer() {
        buffer -= 1;
        System.out.println(buffer);
    }

    boolean canPut() {
        return buffer < max;
    }

    boolean canGet() {
        return buffer > 0;
    }

}

class Producer {

    Buffer buffer;
    Random random = new Random();

    Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    void putProduct() throws InterruptedException {
        synchronized (buffer) {
            if (!buffer.canPut()) {
                System.out.println("producer go sleep");
                buffer.wait();
                System.out.println("producer awake");
            }
            buffer.putToBuffer();
            System.out.println("producer produce an product and put it to buffer");
            buffer.notify();
        }
    }

    void startProducing() throws InterruptedException {
        while(true)
        {
            putProduct();
            TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
        }
    }
}



class Consumer {
    Buffer buffer;
    Random random = new Random();
    Consumer(Buffer buffer)
    {
        this.buffer = buffer;
    }

    void consumeProduct() throws InterruptedException {
        synchronized (buffer)
        {
            if(!buffer.canGet()){
                System.out.println("consumer go sleep");
                buffer.wait();
                System.out.println("consumer awake");
            }
            buffer.getFromBuffer();
            System.out.println("consumer consume a product");
            buffer.notify();
        }
    }

    void startConsume() throws InterruptedException {
        while (true)
        {
            consumeProduct();
            TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
        }
    }
}


public class ThreadTest {
    private static boolean stopRequested = false;
    static Account account = new Account(5000);
    static Boo boo = new Boo();
    static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        int n = 5;
        Thread[] threads = new Thread[n];
        for(int i = 0; i < n; i++){
            threads[i] = new Thread(()->{boo.doWait();});
        }
        Thread t = new Thread(()->{boo.doNotifyAll();});
        for(int i = 0; i < n; i++){
            threads[i].start();
        }
        TimeUnit.SECONDS.sleep(1);
        t.start();

    }


}
