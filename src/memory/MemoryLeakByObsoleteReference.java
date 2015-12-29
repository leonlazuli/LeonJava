package memory;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Created by Leon on 2015/12/9.
 * ����ע��ص����µ�Memory Leak �Լ� weak reference
 * 主要测试的是注册eventHandler之后, 对于它的引用会造成内存泄露
 * 用weakReference可以解决这一点.  但实际上用weakHashTable会更好,因为虽然多了无用的value(比如存个true)什么的, 但是hash的读取速度是常数的.
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
