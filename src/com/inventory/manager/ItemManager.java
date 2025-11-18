package com.inventory.manager;

import com.inventory.model.Item;
import com.inventory.model.ItemLocation;
import com.inventory.model.ItemObserver; // 추가됨

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemManager {
    private static final ItemManager instance = new ItemManager();
    private static final String ITEM_FILE = "items.txt";
    private List<Item> itemList;

    // 옵저버 리스트 추가
    private List<ItemObserver> observers = new ArrayList<>();

    private ItemManager() {
        itemList = FileHandler.loadItems(ITEM_FILE);
    }

    public static ItemManager getInstance() {
        return instance;
    }

    // --- 옵저버 패턴 메서드 ---
    public void addObserver(ItemObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ItemObserver observer) {
        observers.remove(observer);
    }

    private void notifyItemAdded(Item item) {
        for (ItemObserver observer : observers) {
            observer.onItemAdded(item);
        }
    }

    private void notifyItemRemoved(Item item) {
        for (ItemObserver observer : observers) {
            observer.onItemRemoved(item);
        }
    }

    private void notifyItemUpdated(Item item) {
        for (ItemObserver observer : observers) {
            observer.onItemUpdated(item);
        }
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

    // --- 아이템 관리 (옵저버 알림 추가) ---
    public void addItem(Item item) {
        itemList.add(item);
        saveItems();
        notifyItemAdded(item); // 알림 발송
    }

    public void removeItem(Item item) {
        itemList.remove(item);
        saveItems();
        notifyItemRemoved(item); // 알림 발송
    }

    public void updateItem(Item item) {
        // Java List는 객체 참조이므로, item 객체의 필드가 변경되면
        // 리스트 내의 객체도 이미 변경된 상태입니다.
        saveItems();
        notifyItemUpdated(item); // 알림 발송
    }

    private void saveItems() {
        FileHandler.saveItems(ITEM_FILE, itemList);
    }
}