package normal;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by Leon on 2015/12/10.
 */
public class EnumSetAndTableTest
{
    public static void main(String[] args){
        Text.test();
        Set
    }    
}

class Text{
    public enum EnumStyle {BLOD, ITALIC, UNDERLINE}

    public void applyStyle(Set<EnumStyle> styles){
        for(EnumStyle e : styles){
            System.out.println(e);
        }
    }

    public static void test(){
        Text t = new Text();
        t.applyStyle(EnumSet.of(EnumStyle.BLOD, EnumStyle.ITALIC));
    }
}


