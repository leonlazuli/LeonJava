package jvm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by leonlazuli on 2016-01-16.
 * 本质上来说是一个代理模式,下面简单写了一个普通的静态代理类.在单一情景下,达到的效果是一样的.
 * 使用动态代理会动态省生成类似StaticProxySpokesman的类的字节码.
 * 动态代理相对于静态代理的好处是.写动态的代理的时候,完全不需要知道被代理的类和接口.这样可以让原始类和代理类脱离关系,使得代理类可以灵活的重用.
 */
public class DynamicProxyTest
{

    static class DynamicProxy implements InvocationHandler
    {
        Object obj;

        Object bind(Object obj){
            this.obj = obj;
            return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
            System.out.println("this is Leon");
            return method.invoke(obj, args);
        }
    }

    interface ISay{
        void say();
    }

    static class Spokesman implements ISay{
        public void say(){
            System.out.println("hello world");
        }
    }

    static class StaticProxySpokesman implements ISay{
        ISay man;
        StaticProxySpokesman(ISay man){
            this.man = man;
        }

        public void say(){
            System.out.println("this is a static proxy");
            man.say();
        }
    }

    public static void main(String[] args){
        ISay man = (ISay) new DynamicProxy().bind(new Spokesman());
        ISay staticman = new StaticProxySpokesman(new Spokesman());
        man.say();
        man.equals(staticman); // Spokesman的每一个方法,都会按照动态代理中的Invoke调用,所以equal方法也会输出this is leon
        staticman.say();
    }


}
