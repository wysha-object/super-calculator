package cn.com.wysha.main;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static MainGUI mainGUI;
    public static void main(String[] args) throws Exception {
        mainGUI=new MainGUI();
        mainGUI.setSize(Toolkit.getDefaultToolkit().getScreenSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2);
        mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainGUI.setLocationRelativeTo(null);
        mainGUI.setVisible(true);
    }
}
