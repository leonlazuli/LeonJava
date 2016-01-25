import java.util.*;
import java.util.concurrent.*;

public class TestClass2
{
// linux iss#33 
    public final int number;
    public final String name;

    public TestClass2(int number, String name)
    {

        this.number = number;
        this.name = name;
    }

    public void foo()
    {
        Hashtable t = new Hashtable();
        BlockingQueue<String> bq = new ArrayBlockingQueue<String>(10);
        Callable<String> c = ()->{return "helllo";};
        ExecutorService e = Executors.newFixedThreadPool(10);
        Thread tt = new Thread();
        tt.run();
        Thread.interrupted();


    }
}
