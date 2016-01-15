package jvm;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Leon on 2016/1/15.
 */
public class ClassLoaderTest {
    static interface ICounter{
        String getVersion();
        void increase();
    }
    static class Counter implements ICounter{
        private int counter;
        {counter = 0;}

        @Override
        public String getVersion(){
            return "Version1";
        }

        @Override
        public void increase(){
            counter++;
        }

    }



    static LeonClassLoader leonLoader = new LeonClassLoader();

    static URL getClassPath(String classFullName){
        try {
            return new URL("file:/E:/Workshop/LeonJava/out/production/LeonJava/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
        //return ClassLoaderTest.class.getClassLoader().getResource(classFullName.replace('.','/')+".class");
    }

    static URLClassLoader getLoader(){
        return new URLClassLoader(new URL[]{getClassPath(ClassLoaderTest.class.getName())})
        {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                System.out.printf("Loading class %s %n", name);
                if("jvm.ClassFile".equals(name)){
                    System.out.println("here");
                    return findClass(name);
                }
                else {
                    System.out.println("there");
                    return super.loadClass(name);
                }
            }
        };
    }

    public static class LeonClassLoader extends ClassLoader{
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException{
            try{
                String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                InputStream is = getClass().getResourceAsStream(fileName);
                if(is == null){
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
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf('.') + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if(is == null){
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name,b,0,b.length);
                }catch (IOException e){
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Class c = leonLoader.loadClass("jvm.Counter");
        System.out.println(c.getName());

    }
}

class Counter
{

}


