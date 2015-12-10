package memory;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Created by Leon on 2015/12/9.
 * 测试注册回调导致的Memory Leak 以及 weak reference
 */
public class MemoryLeakByObsoleteReference {

    public static void main(String[] args) {
        System.out.println("hello java in git");
        EventTrigger trigger = new EventTrigger();
        for(int i = 0; i < 100000; i++){
            trigger.attachListener(new Wrapper());
        }
        System.gc();
        trigger.triggerEvent();
        System.out.println("main exit");
    }
}

interface EventHandler {
    void handle();
}

class Wrapper implements EventHandler{
    private static class IDAlocator{
        private static int current = 0;
        public static int allocateID(){return current++;}
    }
    final Calendar c = new GregorianCalendar();
    final int id = IDAlocator.allocateID();
    public void handle(){
        System.out.println(String.format("%d handled",id));
    }

    @Override
    public void finalize(){
        System.out.println(String.format("%d is destoryed", id));
    }
}

class EventTrigger {
    List<WeakReference<EventHandler>> handlers = new ArrayList<>();
    void attachListener(EventHandler handler) {
        handlers.add(new WeakReference<EventHandler>(handler));
    }
    void triggerEvent() {
        for (int i = 0, n = handlers.size(); i < n; i++) {
            handlers.get(i).get().handle();
        }
    }
}
