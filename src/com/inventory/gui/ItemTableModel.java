package com.inventory.gui;

import com.inventory.model.Item;

import javax.swing.table.AbstractTableModel;
import java.util.List;


//JTable에 Item 리스트를 표시하기 위한 커스텀 테이블 모델
public class ItemTableModel extends AbstractTableModel {

    private final String[] columnNames = {"물품번호", "종류", "재질", "품질", "위치", "ESG", "단가", "입고일"};
    private List<Item> items;

    public ItemTableModel(List<Item> items) {
        this.items = items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        fireTableDataChanged(); // 데이터 변경 알림
    }

    public Item getItemAt(int rowIndex) {
        return items.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = items.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getItemNumber();
            case 1: return item.getPantsState().toName();
            case 2: return item.getMaterial();
            case 3: return item.getQuality();
            case 4: return item.getLocation().toString();
            case 5: return item.isESG() ? "Y" : "N";
            case 6: return item.getPrice();
            case 7: return item.getEntryDate().toLocalDate().toString();
            default: return null;
        }
    }
}