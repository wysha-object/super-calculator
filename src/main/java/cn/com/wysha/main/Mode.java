package cn.com.wysha.main;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.math.BigInteger;
import java.util.List;

public class Mode {
    public Mode(Element e) throws DocumentException {
        string_length = Integer.parseInt(e.element("length_hex").getText().replaceAll("[\\r|\\n]",""));
        mode_Name = e.element("name").getText().replaceAll("[\\r|\\n]","");
        List<Element> elements=e.element("rules").elements();
        rules = new Rule[elements.size()];
        for (int i = 0; i < rules.length; i++) {
            rules[i] = new Rule(elements.get(i));
        }
    }

    public final int string_length;

    public final String mode_Name;

    private final Rule[] rules;

    public int[] setData(BigInteger v, String num, String[][] data){
        for (Rule rule:rules){
            if (rule.match(num)){
                return rule.setData(v,num,data);
            }
        }
        int[] r = new int[data.length];
        for (int i = 0; i < data.length; i+=3) {
            data[i][0] = "";
            data[i+2][0] = "";
            r[i/2]=16;
        }
        return r;
    }

    @Override
    public String toString(){
        return mode_Name;
    }
}
