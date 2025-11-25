package com.inventory.manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SalesManager {
    private static final SalesManager instance = new SalesManager();
    private static final String SALES_FILE = "sales.txt";

    // 일자별 총 매출 저장
    private Map<LocalDate, Integer> dailySales;

    private SalesManager() {
        dailySales = FileHandler.loadSales(SALES_FILE);
    }

    public static SalesManager getInstance() {
        return instance;
    }

    public void recordSale(int amount, LocalDateTime timestamp) {
        LocalDate date = timestamp.toLocalDate();
        dailySales.put(date, dailySales.getOrDefault(date, 0) + amount);

        // 매출 발생 시마다 즉시 저장
        FileHandler.saveSales(SALES_FILE, dailySales);
    }

    //오늘을 포함한 최근 7일간의 매출 합계를 반환
    public int getWeeklySales() {
        int totalSales = 0;
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            totalSales += dailySales.getOrDefault(date, 0);
        }
        return totalSales;
    }

    public Map<LocalDate, Integer> getDailySalesMap() {
        return new HashMap<>(dailySales);
    }
}