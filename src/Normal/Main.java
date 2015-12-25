package normal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;

class GenericClass<T> {
    T value;

    GenericClass(T value) {
        this.value = value;
    }

    void print() {
        System.out.println(value);

    }

    T getValue() {
        return (T) value;
    }
}


class DataRowNew
{
    private class Column  // 不需要用泛型了
    {
        String name;
        Object value;
        Column(String name)
        {
            this.name = name;
        }
    }

    Map<String,Column> map = new HashMap<>(); // name to column
    //Map<String,Class<?>> nameToTypeMap = new HashMap<>(); // 没用，必须get的时候显示传T，这样才能做类型推导和自动转换
    <T> void putField(Class<T> type,String columnName, T value)
    {
        Column c = map.get(toString());
        if(c == null){
            c = new Column(columnName);
            map.put(columnName,c);
            //nameToTypeMap.put(columnName, type);
        }
        c.value = value;

    }
    <T> T getField(String columnName, Class<T> t)  // 这里必须传个Class<T> 否则无法类型推断
    {
        Column c = map.get(columnName);
        if(c != null){
            //Class<?> type = nameToTypeMap.get(columnName);  // 这个类型是没有用
            return t.cast(c.value);  // T cast()  T 和 T 符合，所以可以自动转换。
        }
        return null;
    }
}

class DataRow {
    Map<Class<?>, Object> map = new HashMap<>();

    <T> void putField(Class<T> type, T value) {
        map.put(type, value);
    }

    <T> T getField(Class<T> type) {
        return type.cast(map.get(type));
    }
}

class ClassA<T> {
    public T value;
    public List<String> str = new ArrayList<>();

    @TestAnnotation(type = TestAnnotation.CheckType.Exception, value = Exception.class)
    public Collection<T> handle(T a, String b) {
        if (a.toString() != "Leon")
            throw new RuntimeException("just a runtime Exception for test");
        System.out.println("In Class A");
        System.out.println(a);
        System.out.println(b);
        return null;
    }

    @TestAnnotation(type = TestAnnotation.CheckType.Boolean, value = Exception.class)
    public int m2() {
        return -1;
    }

    @TestAnnotation(type = TestAnnotation.CheckType.Boolean, value = Exception.class)
    public boolean m3() {
        return true;
    }

}

class ClassB extends ClassA {


}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface TestAnnotation {
    enum CheckType {Exception, Boolean}

    CheckType type() default CheckType.Boolean;

    Class<? extends Exception> value();
}


public class Main {
    public static void printVariable(String... args) {
        for (String s : args) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws Exception
    {
        long start = System.currentTimeMillis();
        int i = 0;
        int j = 0;
        while(++i > 0){j = i;}
        long duration = System.currentTimeMillis() - start;
        System.out.println(duration);
        System.out.println(j);

    }


    void foo() throws RuntimeException {

    }

    static void simpleTest() throws Exception {
        int notTested = 0;
        int tested = 0;
        int passed = 0;
        Method[] ms = ClassA.class.getDeclaredMethods();
        ClassA a = ClassA.class.newInstance();
        for (Method m : ms) {
            if (m.isAnnotationPresent(TestAnnotation.class)) {
                TestAnnotation test = m.getAnnotation(TestAnnotation.class);
                if (test.type() == TestAnnotation.CheckType.Boolean && m.getReturnType() == boolean.class) {
                    tested++;
                    if ((boolean) m.invoke(a)) {
                        passed++;
                        System.out.format("%s pass the test %n", m.toGenericString());
                    } else {
                        System.out.format("%s doesn't pass the test %n", m.toGenericString());
                    }
                } else if (test.type() == TestAnnotation.CheckType.Exception && m.getParameterTypes().length == 2) {
                    tested++;
                    if (InvokeMethod(a, m, test.value(), "Leon", "Lazuli")) {
                        passed++;
                    }
                    tested++;
                    if (InvokeMethod(a, m, test.value(), "Jack", "Spriraw")) ;
                    passed++;
                } else {
                    notTested++;
                }
            }
        }
        
        System.out.printf("tested:%d, passed:%d, noteTest:%d %n", tested, passed, notTested);
    }

    public static boolean InvokeMethod(Object obj, Method m, Class<? extends Exception> expectedException, Object... args) {
        boolean passed = false;
        try {
            m.setAccessible(true);
            m.invoke(obj, args);
        } catch (InvocationTargetException wrappedEX) {
            if (expectedException.isInstance(wrappedEX.getCause())) {
                passed = true;
            }
        } catch (Exception e) {

        } finally {

        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        if (passed) {
            System.out.printf("%s throw %s as expected with parameter %s, Pass! %n", m.toGenericString(), expectedException.toGenericString(), sb.toString());
        } else {
            System.out.printf("%s doesn't throw %s as expected with parameter %s , NOT Pass!%n", m.toGenericString(), expectedException.toGenericString(), sb.toString());
        }

        return passed;
    }
}
