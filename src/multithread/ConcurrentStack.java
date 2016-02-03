package multithread;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Leon on 2016/2/2.
 */
public class ConcurrentStack<E> {
    private final AtomicReference<Node<E>> top = new AtomicReference<>();

    public void put(E value){
        Node<E> newNode = new Node<>(value,null);
        while (true){
            Node<E> curTop = top.get();
            if(top.compareAndSet(curTop, newNode)){
                /* 这里，如果设置之后top被改变了，正确性不受影响
                 即使newNode已经不是栈顶，curTop依然应该是它的next
                 而newNode和curTop这两个局部变量是不变的，可能被
                 其他线程更改的只是共享变量top
                */
                newNode.next = curTop;
                return;
            }
        }
    }

    /* 这种写法相对于上面那种写法更容易理解，而且更有普遍性
        先把要做的改动做好，然后用CompareAndSet来看
        其他线程时候干扰了，如果没有干扰，就向共享变量
        提交改动，否则重试。反正改动都是在本地的，所以
        先改了也无所谓，如果提交没有成功，之前的本地改动
        就相当于是自动放弃了
    */
    public void put2(E value){
        Node<E> newNode = new Node<>(value,null);
        Node<E> curTop;
        do{
            curTop = top.get();
            newNode.next = curTop;
        }while(!top.compareAndSet(curTop,newNode));
    }

    public E pop(){
        while (true){
            Node<E> curTop = top.get();
            //这里即使在返回的时候，top已经不为null，也不会破坏这个stack的状态，只不过是调用者来处理null而已。
            if(curTop == null)
                return null;
            Node<E> curNext = curTop.next;
            if(top.compareAndSet(curTop,curNext)){
                return curNext.value;
            }
        }
    }

    private static class Node<E>{
        public final E value;
        public Node<E> next;

        public Node(E value, Node<E> next){
            this.value = value;
            this.next = next;
        }
    }
}
