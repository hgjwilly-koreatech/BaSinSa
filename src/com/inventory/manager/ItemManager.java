package com.inventory.manager;

import com.inventory.model.Item;
import com.inventory.model.ItemLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemManager {
    private static final ItemManager instance = new ItemManager();
    private static final String ITEM_FILE = "items.txt";
    private List<Item> itemList;

    private ItemManager() {
        itemList = FileHandler.loadItems(ITEM_FILE);
    }

    public static ItemManager getInstance() {
        return instance;
    }

    // --- 아이템 조회 ---
    public List<Item> getAllItems() {
        return new ArrayList<>(itemList); // 원본 리스트 보호
    }

    public List<Item> getNormalItems() {
        return itemList.stream()
                .filter(item -> item.getLocation() == ItemLocation.NORMAL)
                .collect(Collectors.toList());
    }

    public List<Item> getESGItems() {
        return itemList.stream()
                .filter(item -> item.getLocation() == ItemLocation.ESG)
                .collect(Collectors.toList());
    }

    // --- 아이템 관리 ---
    public void addItem(Item item) {
        itemList.add(item);
        saveItems();
    }

    public void removeItem(Item item) {
        itemList.remove(item);
        saveItems();
    }

    public void updateItem(Item item) {
        // Java List는 객체 참조이므로, item 객체의 필드가 변경되면
        // 리스트 내의 객체도 이미 변경된 상태입니다.
        // 따라서 여기서는 파일 저장만 호출하면 됩니다.
        saveItems();
    }

    private void saveItems() {
        FileHandler.saveItems(ITEM_FILE, itemList);
    }
}