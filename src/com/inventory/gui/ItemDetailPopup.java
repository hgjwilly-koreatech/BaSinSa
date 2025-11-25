package com.inventory.gui;

import com.inventory.model.*;
import javax.swing.*;
import java.awt.*;

//아이템 클릭 시 상세 정보와 기능 버튼을 보여주는 JDialog
public class ItemDetailPopup extends JDialog {

    public ItemDetailPopup(MainWindow owner, Item item, Member member) {
        super(owner, "아이템 상세 정보", true); // Modal

        setSize(400, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        //상세 정보 패널
        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailArea.setText(buildDetailString(item));
        add(new JScrollPane(detailArea), BorderLayout.CENTER);

        //기능 버튼 패널
        if (member instanceof IItemManagable) {
            JPanel buttonPanel = createActionButtons((IItemManagable) member, item);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    //IItemManagable에 따른 버튼 생성 로직
    private JPanel createActionButtons(IItemManagable manager, Item item) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 5, 5)); // 세로로 버튼 쌓기
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        boolean isNormalMember = (manager instanceof NormalMember);
        boolean isESGMember = (manager instanceof ESGMember);

        boolean canManage = false;
        if (isNormalMember && item.getLocation() == ItemLocation.NORMAL) canManage = true;
        if (isESGMember && item.getLocation() == ItemLocation.ESG) canManage = true;

        if (canManage) {
            String moveLabel = isNormalMember ? "ESG 재고로 이동" : "일반 재고로 이동";
            JButton moveBtn = new JButton(moveLabel);
            moveBtn.addActionListener(e -> {
                manager.move(item);
                dispose();
            });
            panel.add(moveBtn);

            String outLabel = isNormalMember ? "출고 (판매)" : "폐기";
            JButton outBtn = new JButton(outLabel);

            // ESG 멤버의 폐기 버튼일 경우 빨간색 강조
            if (isESGMember) {
                outBtn.setBackground(Color.red);
                outBtn.setForeground(Color.BLACK);
            } else {
                outBtn.setBackground(Color.green);
                outBtn.setForeground(Color.BLACK);
            }

            outBtn.addActionListener(e -> {
                String msg = isNormalMember ? "해당 상품을 판매(출고) 하시겠습니까?"
                        : "정말로 이 아이템을 폐기하시겠습니까?\n(삭제 후 복구 불가, 매출 미포함)";
                int confirm = JOptionPane.showConfirmDialog(this, msg, outLabel, JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    manager.outgoing(item);
                    dispose();
                }
            });
            panel.add(outBtn);

            // Recycle 버튼 (ESG 멤버 전용)
            if (isESGMember) {
                if (!item.isESG() && item.getType() != PantsType.SHORTS) {
                    JButton recycleBtn = new JButton("ESG 바지로 변경 (재활용)");
                    recycleBtn.addActionListener(e -> {
                        ((ESGMember) manager).recycle(item);
                        dispose();
                    });
                    // 재활용 버튼은 하단에 추가
                    panel.add(recycleBtn);
                }
            }
        } else {
            // 자신의 담당 구역이 아닐 경우
            JLabel infoLabel = new JLabel("이 재고에 대한 관리 권한이 없습니다.", SwingConstants.CENTER);
            panel.add(infoLabel);
        }

        return panel;
    }

    private String buildDetailString(Item item) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("물품 번호: %s\n", item.getItemNumber()));
        sb.append(String.format("바지 종류: %s\n", item.getType()));
        sb.append(String.format("재      질: %s\n", item.getMaterial()));
        sb.append(String.format("품      질: %s\n", item.getQuality()));
        sb.append(String.format("단      가: %d원\n", item.getPrice()));
        sb.append(String.format("재고 위치: %s\n", item.getLocation()));
        sb.append(String.format("ESG 활용: %s\n", item.isESG() ? "완료" : "미완료"));
        sb.append(String.format("입고 일시: %s\n", item.getEntryDate().toString()));
        return sb.toString();
    }
}