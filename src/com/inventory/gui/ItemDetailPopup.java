package com.inventory.gui;

import com.inventory.model.*;

import javax.swing.*;
import java.awt.*;

/**
 * 아이템 클릭 시 상세 정보와 기능 버튼을 보여주는 JDialog
 */
public class ItemDetailPopup extends JDialog {

    private Item item;
    private Member member;
    private MainWindow owner; // 메인 윈도우 참조 (테이블 갱신용)

    public ItemDetailPopup(MainWindow owner, Item item, Member member) {
        super(owner, "아이템 상세 정보", true); // Modal
        this.owner = owner;
        this.item = item;
        this.member = member;

        setSize(350, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // 1. 상세 정보 패널 (JTextArea)
        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailArea.setText(buildDetailString(item));
        add(new JScrollPane(detailArea), BorderLayout.CENTER);

        // 2. 기능 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        add(buttonPanel, BorderLayout.SOUTH);

        // 멤버 타입과 아이템 상태에 따라 버튼 추가
        if (member instanceof NormalMember) {
            if (item.getLocation() == ItemLocation.NORMAL) {
                JButton moveBtn = new JButton("ESG 재고로 이동");
                moveBtn.addActionListener(e -> {
                    ((NormalMember) member).move(item);
                    owner.refreshTableData(); // 메인 테이블 갱신
                    dispose();
                });
                buttonPanel.add(moveBtn);

                JButton sellBtn = new JButton("출고 (판매)");
                sellBtn.addActionListener(e -> {
                    ((NormalMember) member).outgoing(item);
                    owner.refreshTableData();
                    dispose();
                });
                buttonPanel.add(sellBtn);
            }
        } else if (member instanceof ESGMember) {
            if (item.getLocation() == ItemLocation.ESG) {
                if (!item.isESG() && item.getType() != PantsType.SHORTS) {
                    JButton recycleBtn = new JButton("ESG 바지로 변경 (재활용)");
                    recycleBtn.addActionListener(e -> {
                        ((ESGMember) member).recycle(item);
                        owner.refreshTableData();
                        dispose();
                    });
                    buttonPanel.add(recycleBtn);
                }

                if (item.isESG()) {
                    JButton moveBtn = new JButton("일반 재고로 이동");
                    moveBtn.addActionListener(e -> {
                        ((ESGMember) member).move(item);
                        owner.refreshTableData();
                        dispose();
                    });
                    buttonPanel.add(moveBtn);
                }
            }
        }

        // CEO는 기능 버튼 없음 (조회만)
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