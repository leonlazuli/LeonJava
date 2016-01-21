import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

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
    }
}
