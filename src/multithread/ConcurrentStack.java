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
                /* ����������֮��top���ı��ˣ���ȷ�Բ���Ӱ��
                 ��ʹnewNode�Ѿ�����ջ����curTop��ȻӦ��������next
                 ��newNode��curTop�������ֲ������ǲ���ģ����ܱ�
                 �����̸߳��ĵ�ֻ�ǹ������top
                */
                newNode.next = curTop;
                return;
            }
        }
    }

    /* ����д���������������д����������⣬���Ҹ����ձ���
        �Ȱ�Ҫ���ĸĶ����ã�Ȼ����CompareAndSet����
        �����߳�ʱ������ˣ����û�и��ţ����������
        �ύ�Ķ����������ԡ������Ķ������ڱ��صģ�����
        �ȸ���Ҳ����ν������ύû�гɹ���֮ǰ�ı��ظĶ�
        ���൱�����Զ�������
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
            //���Ｔʹ�ڷ��ص�ʱ��top�Ѿ���Ϊnull��Ҳ�����ƻ����stack��״̬��ֻ�����ǵ�����������null���ѡ�
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
