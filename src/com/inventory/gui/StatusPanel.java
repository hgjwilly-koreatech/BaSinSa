package com.inventory.gui;

import com.inventory.manager.ItemManager;
import com.inventory.model.Item;
import com.inventory.model.ItemObserver;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

// 화면 하단에 현재 재고 현황을 실시간으로 보여주는 패널
// ItemObserver를 구현하여 데이터 변경 시 자동으로 갱신됨

public class StatusPanel extends JPanel implements ItemObserver {

    private JLabel totalLabel;
    private JLabel normalLabel;
    private JLabel esgLabel;

    public StatusPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setBackground(Color.lightGray);

        totalLabel = new JLabel("총 재고: 0");
        totalLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        normalLabel = new JLabel("일반: 0");
        normalLabel.setForeground(Color.BLUE);

        esgLabel = new JLabel("ESG: 0");
        esgLabel.setForeground(Color.GREEN);

        add(totalLabel);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(normalLabel);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(esgLabel);

        ItemManager.getInstance().addObserver(this); // 옵저버 등록
        updateStatus(); // 초기값 설정하기
    }

    private void updateStatus() {
        List<Item> allItems = ItemManager.getInstance().getAllItems();
        long normalCount = ItemManager.getInstance().getNormalItems().size();
        long esgCount = ItemManager.getInstance().getESGItems().size();

        totalLabel.setText("총 재고: " + allItems.size() + "개");
        normalLabel.setText("일반: " + normalCount + "개");
        esgLabel.setText("ESG: " + esgCount + "개");
    }

    // 옵저버 인터페이스 구현
    @Override
    public void onItemAdded(Item item) {
        updateStatus();
    }

    @Override
    public void onItemRemoved(Item item) {
        updateStatus();
    }

    @Override
    public void onItemUpdated(Item item) {
        updateStatus();
    }
}