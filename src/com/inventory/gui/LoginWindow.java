package com.inventory.gui;

import com.inventory.manager.LoginManager;
import com.inventory.model.Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginWindow extends JFrame {

    public LoginWindow() {
        setTitle("👖 재고 관리 프로그램 - 로그인");
        setSize(450, 250); // 크기 확대
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setLayout(new BorderLayout(20, 20)); // 간격 확대

        // 메인 컨테이너 패널
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // 상하좌우 여백 넉넉히

        // 입력 필드 패널
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 15)); // 수직 간격 15

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JTextField idField = new JTextField();
        idField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JPasswordField pwField = new JPasswordField();
        pwField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        fieldsPanel.add(idLabel);
        fieldsPanel.add(idField);
        fieldsPanel.add(pwLabel);
        fieldsPanel.add(pwField);

        mainPanel.add(fieldsPanel, BorderLayout.CENTER);

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(0, 50)); // 버튼 높이 키움

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

        add(mainPanel, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);
    }
}