package com.inventory.gui;

import com.inventory.manager.LoginManager;
import com.inventory.model.Member;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    public LoginWindow() {
        setTitle("👖 재고 관리 프로그램 - 로그인");
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setLayout(new BorderLayout(10, 10));

        // 패널 생성
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 컴포넌트 생성
        panel.add(new JLabel("아이디:"));
        JTextField idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("비밀번호:"));
        JPasswordField pwField = new JPasswordField();
        panel.add(pwField);

        JButton loginButton = new JButton("로그인");

        // 로그인 버튼 이벤트
        loginButton.addActionListener(e -> {
            String id = idField.getText();
            String password = new String(pwField.getPassword());

            Member loggedInMember = LoginManager.getInstance().login(id, password);

            if (loggedInMember != null) {
                // 로그인 성공
                JOptionPane.showMessageDialog(this, loggedInMember.getName() + "님, 환영합니다.");
                new MainWindow(loggedInMember).setVisible(true); // 메인 윈도우 열기
                this.dispose(); // 현재 로그인 창 닫기
            } else {
                // 로그인 실패
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 엔터 키로 로그인
        this.getRootPane().setDefaultButton(loginButton);

        add(panel, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);
    }
}