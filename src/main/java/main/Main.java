package main;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        MainGUI mainGUI=new MainGUI();
        mainGUI.setSize(Toolkit.getDefaultToolkit().getScreenSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2);
        mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainGUI.setLocationRelativeTo(null);
        mainGUI.setVisible(true);
    }
}
