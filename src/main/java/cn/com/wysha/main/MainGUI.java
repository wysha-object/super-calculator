package cn.com.wysha.main;

import cn.com.wysha.ui.WarningView;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MainGUI extends JFrame {
    final File file = new File("./data.xml");
    BigInteger bigInteger;
    int max_l = 4;
    boolean change = true;
    String[][] data = new String[0][0];
    int[] nums = new int[0];
    Mode[] modes;
    Mode mode;
    private JPanel contentPane;
    private JTextField textField;
    private JButton aButton;
    private JButton bButton;
    private JButton cButton;
    private JButton dButton;
    private JButton eButton;
    private JButton fButton;
    private JButton a7Button;
    private JButton a8Button;
    private JButton a9Button;
    private JButton a4Button;
    private JButton a5Button;
    private JButton a6Button;
    private JButton a1Button;
    private JButton a2Button;
    private JButton a3Button;
    private JButton a0Button;
    private JPanel tablePane;
    private JComboBox<Mode> comboBox;
    private JButton flushButton;
    private JButton cleanButton;
    private JButton delButton;

    private void writeDefault(){
        try {
            Document document = DocumentHelper.createDocument();
            Element root=document.addElement("modes");
            Element modeElement=root.addElement("mode");
            modeElement.addElement("length_hex").addText("4");
            modeElement.addElement("name").addText("段选择子(Segment_Selector)");
            Element rulesElement=modeElement.addElement("rules").addElement("rule");
            rulesElement.addElement("expression").addText(".*");
            Element rules = rulesElement.addElement("rules");
            Element element;
            Element e;

            element=rules.addElement("rule");
            element.addElement("length").addText("13");
            element.addElement("name").addText("段描述符索引[13-0]");
            e = element.addElement("rules").addElement("rule");
            e.addElement("expression").addText(".*");
            e.addElement("description").addText("%value (16, auto)%");

            element=rules.addElement("rule");
            element.addElement("length").addText("1");
            element.addElement("name").addText("TI");
            e = element.addElement("rules").addElement("rule");
            e.addElement("expression").addText(".*");
            e.addElement("description").addText("%value (2, auto)%");

            element=rules.addElement("rule");
            element.addElement("length").addText("2");
            element.addElement("name").addText("RPL");
            e = element.addElement("rules").addElement("rule");
            e.addElement("expression").addText(".*");
            e.addElement("description").addText("%value (2, auto)%");

            XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            writer.write(document);
            writer.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private void read(){
        try {
            Document document = new SAXReader().read(file);
            Element root = document.getRootElement();
            java.util.List<Element> elements = root.elements();
            modes = new Mode[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                modes[i]=new Mode(element);
            }
        } catch (DocumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    public MainGUI() throws Exception {
        try {
            if (!file.exists()){
                file.createNewFile();
                writeDefault();
            }

            read();

            setContentPane(contentPane);
            setMinimumSize(new Dimension(1440, 720));

            flushButton.addActionListener(e -> {
                change = true;
                flush();
            });

            textField.setBorder(null);
            textField.setText("0000");

            for (Mode m:modes){
                comboBox.addItem(m);
            }
            comboBox.addItemListener(e -> setMode((Mode) e.getItem()));

            aButton.addActionListener(e -> textField.setText(textField.getText() + "A"));
            bButton.addActionListener(e -> textField.setText(textField.getText() + "B"));
            cButton.addActionListener(e -> textField.setText(textField.getText() + "C"));
            dButton.addActionListener(e -> textField.setText(textField.getText() + "D"));
            eButton.addActionListener(e -> textField.setText(textField.getText() + "E"));
            fButton.addActionListener(e -> textField.setText(textField.getText() + "F"));
            a7Button.addActionListener(e -> textField.setText(textField.getText() + "7"));
            a8Button.addActionListener(e -> textField.setText(textField.getText() + "8"));
            a9Button.addActionListener(e -> textField.setText(textField.getText() + "9"));
            a4Button.addActionListener(e -> textField.setText(textField.getText() + "4"));
            a5Button.addActionListener(e -> textField.setText(textField.getText() + "5"));
            a6Button.addActionListener(e -> textField.setText(textField.getText() + "6"));
            a1Button.addActionListener(e -> textField.setText(textField.getText() + "1"));
            a2Button.addActionListener(e -> textField.setText(textField.getText() + "2"));
            a3Button.addActionListener(e -> textField.setText(textField.getText() + "3"));
            a0Button.addActionListener(e -> textField.setText(textField.getText() + "0"));

            cleanButton.addActionListener(e -> textField.setText(""));
            delButton.addActionListener(e -> textField.setText(textField.getText().substring(0, textField.getText().length() - 1)));

            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (String.valueOf(e.getKeyChar()).matches("[^0-9a-fA-F]")) {
                        e.consume();
                    }
                }
            });

            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update();
                }

                private void update() {
                    change = true;
                    SwingUtilities.invokeLater(() -> flush());
                }
            });

            this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    change = true;
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                    change = true;
                }
            });

            change = true;
            Timer cooldownTimer = new Timer(100, event -> flush());
            cooldownTimer.start();

            setMode(modes[0]);
        }catch (Exception exception){
            new WarningView("启动失败\r\n./Data.xml配置文件可能格式不符合要求或无法读写");
            throw exception;
        }
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        max_l=mode.string_length;
        change = true;
        flush();
    }

    boolean e=true;
    public synchronized void flush() {
        try {
            if (change) {
                change = false;
                if (e){
                    e=false;
                }else {
                    String text = textField.getText();
                    if (text.matches("[^0-9A-F]+")||text.length()!=max_l) {
                        text=text.toUpperCase().replaceAll("[^0-9A-F]+","");
                        if (text.isEmpty()) {
                            text="0".repeat(max_l);
                        } else if (text.length() > max_l) {
                            text=text.substring(text.length() - max_l);
                        } else if (text.length() < max_l) {
                            text="0".repeat(max_l - text.length()) + text;
                        }
                        textField.setText(text);
                        e=true;
                    }

                    data = new String[max_l / 4 * 3][16];

                    bigInteger = new BigInteger(text, 16);

                    for (int j = 0; j < max_l / 4; ++j) {
                        for (int index = 0; index < 16; ++index) {
                            data[data.length - j * 3 - 2][15-index] = j * 16 + index>bigInteger.bitLength()?"0":(bigInteger.testBit(j * 16 + index)?"1":"0");
                        }
                    }

                    String s=bigInteger.toString(2);
                    nums=mode.setData(bigInteger,"0".repeat(max_l*4-s.length())+s, data);
                    setTable();

                    contentPane.revalidate();
                }
            }
            contentPane.repaint();
        }catch (Exception exception){
            new WarningView("刷新失败\r\n请检查./Data.xml配置文件");
            throw exception;
        }
    }

    private void setTable() {
        tablePane.removeAll();
        tablePane.setMinimumSize(new Dimension(-1, -1));

        int height = tablePane.getHeight() / max_l * 4 / 12 - 1;
        int width = tablePane.getWidth() / 16;

        int a = 0;
        for (int i = 0; i < data.length / 3; i += 1) {
            HashMap<Integer,Color> hashMap=new HashMap<>();
            int k = a;
            for (int f = 0; f < 3; f += 2) {
                k = a;
                for (int j = 0; j < 16; j += nums[k], k++) {
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridwidth = nums[k];
                    gbc.gridheight = 1;
                    gbc.gridx = j;
                    gbc.gridy = i * 5 + 1 + f;
                    gbc.weightx = 1;
                    gbc.weighty = 1;
                    gbc.fill = GridBagConstraints.BOTH;

                    hashMap.put(j,data[i * 3][j].isEmpty()?new Color(223,223,223):new Color(255,255,255));

                    JPanel panel;
                    panel = newJPanel(f, width * nums[k], height * 4, 4 + (int) ((double) width / (max_l / 8 > 1 ? (((double) max_l / 8 - 1) / 2 + 1) : (1 - (1 - (double) max_l / 8) / 2)) / 7), data[i * 3 + f][j],hashMap.get(j));
                    tablePane.add(panel, gbc);
                }
            }
            a = k;
            int key=0;
            for (int j = 0; j < 16; j++) {
                if (hashMap.containsKey(j)){
                    key=j;
                }
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                gbc.gridx = j;
                gbc.gridy = i * 5;
                gbc.weightx = 0;
                gbc.weighty = 0;
                gbc.fill = GridBagConstraints.BOTH;
                JPanel panel = newJPanel(0, width, height, height / 2, String.valueOf(max_l * 4 - i * 16 - 1 - j),hashMap.get(key));
                tablePane.add(panel, gbc);
                gbc = new GridBagConstraints();
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                gbc.gridx = j;
                gbc.gridy = i * 5 + 2;
                gbc.weightx = 1;
                gbc.weighty = 1;
                gbc.fill = GridBagConstraints.BOTH;
                panel = newJPanel(1, width, height * 2, height, data[i * 3 + 1][j],hashMap.get(key));
                tablePane.add(panel, gbc);
            }
            if (i + 3 < data.length) {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = 16;
                gbc.gridheight = 1;
                gbc.gridx = 0;
                gbc.gridy = i * 5 + 4;
                gbc.weightx = 1;
                gbc.weighty = 1;
                gbc.fill = GridBagConstraints.BOTH;
                JPanel jPanel = new JPanel();
                jPanel.setBackground(new Color(243,243,243));
                tablePane.add(jPanel, gbc);
            }
        }
    }

    private JPanel newJPanel(int i, int width, int height, int size, String text , Color color) {
        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBackground(color);

        jPanel.setMinimumSize(new Dimension(width, height));
        jPanel.setPreferredSize(new Dimension(width, height));
        jPanel.setMaximumSize(new Dimension(width, height));
        jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JTextPane jTextPane = new JTextPane();
        jTextPane.setText(text);
        jTextPane.setBackground(jPanel.getBackground());
        jTextPane.setEditable(false);
        jTextPane.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, size));

        StyledDocument doc = jTextPane.getStyledDocument();
        SimpleAttributeSet centerAttr = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttr, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), centerAttr, false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        switch (i) {
            case 0 -> gbc.anchor = GridBagConstraints.SOUTH;
            case 1 -> gbc.anchor = GridBagConstraints.CENTER;
            case 2 -> gbc.anchor = GridBagConstraints.NORTH;
        }
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jPanel.add(jTextPane, gbc);

        return jPanel;
    }
}
