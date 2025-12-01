package com.inventory.gui;

import com.inventory.manager.SalesManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SalesManagementPanel extends JPanel {

    public SalesManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<LocalDate, Integer> salesMap = SalesManager.getInstance().getDailySalesMap();

        //데이터 준비 (전체)
        String[] columnNames = {"날짜", "매출액(원)"};
        DefaultTableModel salesModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        LocalDate today = LocalDate.now();
        // 오늘 포함 7일간 (오늘 ~ 6일전)
        LocalDate startOfWeekly = today.minusDays(6);

        long weeklyTotal = 0;
        long grandTotal = 0;
        NumberFormat nf = NumberFormat.getInstance();

        // 날짜 정렬 (최신순)
        List<LocalDate> sortedDates = new ArrayList<>(salesMap.keySet());
        sortedDates.sort((d1, d2) -> d2.compareTo(d1)); // 내림차순

        for (LocalDate date : sortedDates) {
            int amount = salesMap.get(date);
            salesModel.addRow(new Object[]{date.toString(), nf.format(amount)});

            grandTotal += amount;
            // 최근 7일 매출 합계 계산
            if (!date.isBefore(startOfWeekly) && !date.isAfter(today)) {
                weeklyTotal += amount;
            }
        }

        //테이블 생성
        JTable salesTable = new JTable(salesModel);
        salesTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        salesTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        //하단 정보 패널
        JPanel bottomPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 통계 라벨 패널 (일렬 배치)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JLabel weeklyLabel = new JLabel("최근 7일 매출: " + nf.format(weeklyTotal) + "원");
        weeklyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

        JLabel totalLabel = new JLabel("총 누적 매출: " + nf.format(grandTotal) + "원");
        totalLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

        statsPanel.add(weeklyLabel);
        statsPanel.add(new JSeparator(SwingConstants.VERTICAL));
        statsPanel.add(totalLabel);

        bottomPanel.add(statsPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
