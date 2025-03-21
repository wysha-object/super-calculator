package cn.com.wysha.ui;

import cn.com.wysha.main.Main;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class WarningView extends JFrame{
    private static WarningView warningView;
    private JPanel contentPanel;
    private JTextPane textPane;

    public WarningView(String warn){
        if (Main.mainGUI!=null){
            Main.mainGUI.setVisible(false);
        }
        if (warningView==null){
            textPane.setText(warn);
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet centerAttr = new SimpleAttributeSet();
            StyleConstants.setAlignment(centerAttr, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), centerAttr, false);

            setAlwaysOnTop(true);
            setContentPane(contentPanel);
            setSize(Toolkit.getDefaultToolkit().getScreenSize().width/4,Toolkit.getDefaultToolkit().getScreenSize().height/4);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
            warningView=this;
        }
    }
}
