package com.inventory;

import com.inventory.gui.LoginWindow;

import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // OS 기본 테마 적용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
    }
}