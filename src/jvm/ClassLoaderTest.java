package jvm;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Leon on 2016/1/15.
 * -- 每个类一定与加载它的加载器绑定,并有它和自己的全类名来确定唯一性.
 * -- 类的加载时递归的, 每个加载器会缓存加载过的类.
 * -- 具体的坑见##
 */
public class ClassLoaderTest {
    static int data = 33;
    public static int publicData = 3333;
    static LeonClassLoader leonLoader = new LeonClassLoader();

    public static interface ISay
    {
        void say();
    }

    public static class LeonSpokesman implements ISay{
        @Override
        public void say(){
            //##用自定义加载器加载的类的实例调用print的语句的时候,也会触发用自定义类加载器加载System等类(虽然最终是委托给了系统默认加载器)
            System.out.println("hello world, from Leon");
            System.out.println(publicData);
            //## 因为main所在的类是由默认加载器加载的,所以这里的包级私有静态变量就无法被LeonClassLoader加载的类的实例访问到.
            System.out.println(data);
        }
    }



    public static class LeonClassLoader extends ClassLoader{
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException{
            try{
                System.out.printf("Loading %s %n", name);
                String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                InputStream is = getClass().getResourceAsStream(fileName);

                // ## 这里HardCode,强制只用这个类加载器加载Spokesman类,其他的全部委托的给默认加载器
                if(is == null || !name.contains("Spokesman")){
                    return super.loadClass(name);
                }
                byte[] b = new byte[is.available()];
                is.read(b);
                return defineClass(name,b,0,b.length);
            }catch (IOException e){
                throw new ClassNotFoundException(name);
            }
        }
    }


    public static void main(String[] args)throws Exception{
        //ClassLoaderTest类是由默认加载器加载的.
        Class c = leonLoader.loadClass("jvm.ClassLoaderTest$LeonSpokesman");
        ISay obj = (ISay) c.newInstance();
        ISay obj2 = new LeonSpokesman();
        System.out.println(obj.getClass().getClassLoader().getClass().getName());
        System.out.println(obj2.getClass().getClassLoader().getClass().getName());
        obj2.say();
        obj.say();
        //System.out.println(c.getName());
        //System.out.println(c.getClassLoader().getClass().getName());

    }

    //    static interface ICounter{
//        String getVersion();
//        void increase();
//    }
//    static class Counter implements ICounter{
//        private int counter;
//        {counter = 0;}
//
//        @Override
//        public String getVersion(){
//            return "Version1";
//        }
//
//        @Override
//        public void increase(){
//            counter++;
//        }
//
//    }
//    static URL getClassPath(String classFullName){
//        try {
//            return new URL("file:/E:/Workshop/LeonJava/out/production/LeonJava/");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return null;
//        //return ClassLoaderTest.class.getClassLoader().getResource(classFullName.replace('.','/')+".class");
//    }
//
//    static URLClassLoader getLoader(){
//        return new URLClassLoader(new URL[]{getClassPath(ClassLoaderTest.class.getName())})
//        {
//            @Override
//            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//                System.out.printf("Loading class %s %n", name);
//                if("jvm.ClassFile".equals(name)){
//                    System.out.println("here");
//                    return findClass(name);
//                }
//                else {
//                    System.out.println("there");
//                    return super.loadClass(name);
//                }
//            }
//        };
//    }

}




