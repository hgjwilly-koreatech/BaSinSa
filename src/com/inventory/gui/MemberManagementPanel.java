package com.inventory.gui;

import com.inventory.manager.AccountManager;
import com.inventory.model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberManagementPanel extends JPanel {
    private DefaultTableModel memberTableModel;
    private JTable memberTable;

    public MemberManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. 상단 사원 리스트 테이블 ---
        String[] columnNames = {"유형", "ID", "비밀번호", "이름"};
        memberTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        memberTable = new JTable(memberTableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        memberTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(memberTable);
        add(scrollPane, BorderLayout.CENTER);

        loadTable();

        // --- 2. 하단 버튼 패널 ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 100));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        Dimension btnDim = new Dimension(90, 40);

        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 30));

        JButton addBtn = new JButton("추가");
        addBtn.setPreferredSize(btnDim);
        addBtn.setBackground(new Color(34, 139, 34));
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        JButton delBtn = new JButton("삭제");
        delBtn.setPreferredSize(btnDim);
        delBtn.setBackground(new Color(220, 20, 60));
        delBtn.setForeground(Color.BLACK);
        delBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        leftBtnPanel.add(addBtn);
        leftBtnPanel.add(delBtn);
        
        bottomPanel.add(leftBtnPanel, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Normal", "ESG"});
            JTextField idField = new JTextField();
            JTextField pwField = new JTextField();
            JTextField nameField = new JTextField();

            inputPanel.add(new JLabel("사원 유형:"));
            inputPanel.add(typeCombo);
            inputPanel.add(new JLabel("ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("비밀번호:"));
            inputPanel.add(pwField);
            inputPanel.add(new JLabel("이름:"));
            inputPanel.add(nameField);

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "새 사원 등록", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String type = (String) typeCombo.getSelectedItem();
                String id = idField.getText().trim();
                String pw = pwField.getText().trim();
                String name = nameField.getText().trim();

                if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "모든 정보를 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    AccountManager.getInstance().addMember(type, id, pw, name);
                    loadTable();
                    JOptionPane.showMessageDialog(this, "사원이 추가되었습니다.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "추가 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        delBtn.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "삭제할 사원을 목록에서 선택해주세요.", "선택 필요", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String type = (String) memberTableModel.getValueAt(selectedRow, 0);
            String id = (String) memberTableModel.getValueAt(selectedRow, 1);
            String name = (String) memberTableModel.getValueAt(selectedRow, 3);

            if ("CEO".equals(type)) {
                JOptionPane.showMessageDialog(this, "CEO 계정은 삭제할 수 없습니다.", "삭제 불가", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "[" + type + "] " + name + " (" + id + ") 사원을 정말 삭제하시겠습니까?",
                    "삭제 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                AccountManager.getInstance().removeMember(id);
                loadTable();
                JOptionPane.showMessageDialog(this, "삭제되었습니다.");
            }
        });
    }

    private void loadTable()
    {
        memberTableModel.setRowCount(0);
        List<Member> members = AccountManager.getInstance().getMemberList();
        for (Member m : members) {
            memberTableModel.addRow(new Object[]{m.getMemberType(), m.getId(), m.getPassword(), m.getName()});
        }
    }
}
