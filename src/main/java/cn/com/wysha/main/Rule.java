package cn.com.wysha.main;

import cn.com.wysha.ui.WarningView;
import org.dom4j.Element;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum ParseStringRule {
    NEWLINE {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, "\r\n");
            return 0;
        }
    },
    VALUE {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            if (strings.get(index + 3).equals("AUTO")) {
                strings.set(index, v.shiftRight(i - length).and(new BigInteger("1".repeat(length), 2)).toString(Integer.parseInt(strings.get(index + 2))));

                return 4;
            } else {
                int start = Integer.parseInt(strings.get(index + 3));
                int end = Integer.parseInt(strings.get(index + 4));
                strings.set(index, v.shiftRight(start).and(new BigInteger("1".repeat(end - start), 2)).toString(Integer.parseInt(strings.get(index + 2))));

                return 5;
            }
        }
    },
    ADD {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).add(new BigInteger(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    SUB {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).subtract(new BigInteger(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    MUL {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).multiply(new BigInteger(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    DIV {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).divide(new BigInteger(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    AND {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).and(new BigInteger(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    OR {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).or(new BigInteger(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    XOR {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).xor(new BigInteger(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    SL {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).shiftLeft(Integer.parseInt(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    },
    SR {
        @Override
        int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length) {
            strings.set(index, new BigInteger(strings.get(index + 3), Integer.parseInt(strings.get(index + 2))).shiftRight(Integer.parseInt(strings.get(index + 4), Integer.parseInt(strings.get(index + 2)))).toString(Integer.parseInt(strings.get(index + 2))));
            return 5;
        }
    };

    abstract int parse(int i, ArrayList<String> strings, int index, BigInteger v, int length);
}

public class Rule {
    private final String rule;
    private final Data[] data;
    private final int[] r;

    public Rule(Element element) {
        rule = element.element("expression").getText().replaceAll("\\s","");
        List<Element> elements = element.element("rules").elements();
        data = new Data[elements.size()];
        r = new int[elements.size()];
        for (int i = 0; i < data.length; i++) {
            Element ruleElement = elements.get(i);
            List<Element> ruleElements = ruleElement.element("rules").elements();

            String[] rules = new String[ruleElements.size()];
            String[] descriptions = new String[ruleElements.size()];
            for (int j = 0; j < ruleElements.size(); j++) {
                rules[j] = ruleElements.get(j).element("expression").getText().replaceAll("\\s","");
                descriptions[j] = ruleElements.get(j).element("description").getText().replaceAll("\\s","");
            }

            int l = Integer.parseInt(ruleElement.element("length").getText().replaceAll("\\s",""));

            r[i] = l;
            data[i] = new Data(l, ruleElement.element("name").getText().replaceAll("\\s",""), rules, descriptions);
        }
    }

    public boolean match(String num) {
        return num.matches(rule);
    }

    public int[] setData(BigInteger v, String num, String[][] data) {
        int x = 0;
        int y = 0;
        Data[] data1 = this.data;
        for (Data value : data1) {
            x += value.setData(x, y*3, v, num, data);
            if (x == 16) {
                x=0;
                ++y;
            } else if (x > 16) {
                new WarningView("发现不符合要求的长度\r\n请检查./Data.xml配置文件的length节点");
                throw new RuntimeException();
            }
        }
        return r;
    }
}

record Data(int length, String name, String[] rules, String[] descriptions) {
    public int setData(int startX, int startY, BigInteger v, String num, String[][] data) {
        data[startY][startX] = name;
        String[] strings = this.rules;
        for (int i = 0; i < strings.length; i++) {
            String d = strings[i];
            if (num.matches(d)) {
                StringBuilder stringBuilder = new StringBuilder();
                boolean super_mode = false;
                char[] charArray = descriptions[i].toCharArray();
                StringBuilder s = new StringBuilder();
                for (char c : charArray) {
                    if (c == '%') {
                        super_mode = !super_mode;
                        if (!s.isEmpty()) {
                            stringBuilder.append(parse((num.length() / 16 - 1 - startY / 3) * 16 + 16 - startX, s.toString(), v));
                            s = new StringBuilder();
                            continue;
                        }
                    }
                    if (super_mode) {
                        if (c != '%') {
                            s.append(c);
                        }
                    } else {
                        stringBuilder.append(c);
                    }
                }
                data[startY + 2][startX] = stringBuilder.toString();
                return length;
            }
        }
        data[startX][startY + 2] = "";
        return length;
    }

    public String parse(int i, String s, BigInteger v) {
        ArrayList<String> strings = new ArrayList<>(Arrays.stream(s.toUpperCase().replaceAll("\\(", " ( ").replaceAll("\\)", " ) ").split("[\\s|,]+")).toList());

        parseString(i, strings, 0, v);

        StringBuilder r = new StringBuilder();
        for (String string : strings) {
            r.append(string);
        }

        return r.toString();
    }

    private void parseString(int i, ArrayList<String> strings, int index, BigInteger v) {
        for (int j = index + 2; j < strings.size(); j++) {
            if (strings.get(j).equals("(")) {
                parseString(i, strings, j - 1, v);
            }
            if (strings.get(j).equals(")")) {
                break;
            }
        }
        for (ParseStringRule parseStringRule : ParseStringRule.values()) {
            if (strings.get(index).equals(parseStringRule.name())) {
                int n = parseStringRule.parse(i, strings, index, v, length);
                for (int j = 0; j < n; j++) {
                    strings.remove(index + 1);
                }
                break;
            }
        }
    }
}