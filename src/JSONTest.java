import java.util.*;

/**
 * Created by leonlazuli on 2015-12-07.
 */

public class JSONTest {

    
    public static void main(String[] args){
        String jsonString = "{\"firstName\":\"John\",\"lastName\":\"Smith\",\"age\":25,\"address\":{\"streetAddress\":\"21 2nd Street\",\"city\":\"New York\",\"state\":\"NY\",\"postalCode\":\"10021-3100\"},\"phoneNumbers\":[{\"type\":\"home\",\"number\":\"212 555-1234\"},{\"type\":\"office\",\"number\":\"646 555-4567\"}],\"children\":[]}";
        JSONDecoder.IJSONValue value =  JSONDecoder.parse(jsonString);
        System.out.println(value);
    }

    

    public static class JSONDecoder {

        private static class Iter {
            private final String string;
            private final int length;
            private int index = -1;
            private char current = '\0';

            public Iter(String s) {
                this.string = s;
                this.length = s.length();
                //System.out.println(s);
            }

            public boolean hasNext() {
                while ((index + 1) < length && Character.isWhitespace(string.charAt(index + 1)))
                    ++index;
                return index + 1 < length;
            }

            public char next() {
                while (++index < length)
                    if (!Character.isWhitespace(string.charAt(index)))
                        return current = string.charAt(index);
                throw new RuntimeException("next() out of range");
            }

            public char current() {
                return current;
            }
        }

        private interface IJSONValue {

        }

        private class JSONObject implements IJSONValue {
            final private Map<String, IJSONValue> map = new HashMap<>();

            @Override
            public String toString(){
                StringBuilder sb = new StringBuilder();
                String comma = "";
                sb.append("{ ");
                for(Map.Entry<String,IJSONValue> entry : map.entrySet()){
                    sb.append(comma);
                    sb.append(entry.getKey());
                    sb.append(":");
                    sb.append(entry.getValue());
                    comma = ", ";
                }
                sb.append("}");
                return sb.toString();
            }
        }

        private class JSONList implements IJSONValue {
            final private List<IJSONValue> list = new ArrayList<>();

            @Override
            public String toString(){
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                String comma = "";
                for(IJSONValue value : list){
                    sb.append("");
                    sb.append(value);
                    comma = ",";
                }
                sb.append("]");
                return sb.toString();
            }
        }

        private class JSONNumber implements IJSONValue {
            final private int value; // int as example, in reality should inculde int double long short etc.
            public JSONNumber(int value){
                this.value = value;
            }

            @Override
            public String toString(){
                return String.format("%d", value);
            }
        }

        private class JSONString implements IJSONValue {
            final private String value;

            public JSONString(String value){
                this.value = value;
            }

            @Override
            public String toString(){
                return value;
            }
        }

        private final Iter iter;
        private IJSONValue root = null;

        public JSONDecoder(String jsonString) {
            this.iter = new Iter(jsonString);
        }

        private void expect(char c) {
            if (iter.current() != c)
                throw new RuntimeException(String.format("syntax error, expect %c, get %c", c, iter.current()));
            else if(iter.hasNext())
                iter.next();
        }

        private IJSONValue parseNextJSONValue() {
            IJSONValue ret = null;
            if (iter.current() == '{')
                ret = parseObject();
            else if (iter.current() == '\"')
                ret = parseString();
            else if (Character.isDigit(iter.current()))
                ret = parseNumber();
            else if (iter.current() == '[')
                ret = parseList();
            else
                throw new RuntimeException(String.format("unknown character %c", iter.current()));
            return ret;
        }

        private IJSONValue parse() // TODO type of return value
        {
            if (!iter.hasNext())
                return null;
            iter.next();
            root = parseNextJSONValue();
            return root;
        }

        // �൱��acceptд��ÿ��������,�����Ƿ���ÿ��������,����û������ʦ���ַ�����,������д����.
        private IJSONValue parseObject() {
            JSONObject obj = new JSONObject();
            expect('{');
            if (iter.current() == '}') {
                expect('}');
                return obj;
            }
            while (iter.hasNext()) {
                String key = parseRawString();
                expect(':');
                IJSONValue value = parseNextJSONValue();
                obj.map.put(key, value);
                if (iter.current() == '}') {
                    expect('}');
                    return obj;
                } else {
                    expect(',');
                }
            }
            throw new RuntimeException("invalid in parse JSONObject");
        }

        private IJSONValue parseList() {
            JSONList list = new JSONList();
            expect('[');
            if(iter.current() == ']'){
                expect(']');
                return list;
            }
            while (iter.hasNext()){
                IJSONValue value = parseNextJSONValue();
                list.list.add(value);
                if(iter.current() == ']'){
                    expect(']');
                    return list;
                }
                else{
                    expect(',');
                }
            }
            throw new RuntimeException("invalid in parse JSONList");
        }

        private IJSONValue parseNumber() {   // int as example for now
            StringBuilder sb = new StringBuilder();
            sb.append(iter.current());
            while (Character.isDigit(iter.next())){
                sb.append(iter.current());
            }
            return new JSONNumber(Integer.parseInt(sb.toString()));
        }

        private IJSONValue parseString() {
            return new JSONString(parseRawString());
        }

        private String parseRawString() {
            StringBuilder sb = new StringBuilder();
            expect('"');
            while (iter.current() != '"')
            {
                sb.append(iter.current());
                iter.next();
            }
            expect('"');
            return sb.toString();
        }


        public static IJSONValue parse(String jsonString) {
            JSONDecoder d = new JSONDecoder(jsonString);
            return d.parse();
        }
    }
}