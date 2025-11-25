package com.inventory.manager;

import com.inventory.model.Item;
import com.inventory.model.ItemLocation;
import com.inventory.model.ItemObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemManager {
    private static final ItemManager instance = new ItemManager();
    private static final String ITEM_FILE = "items.txt";
    private List<Item> itemList;

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
        return new ArrayList<>(itemList);
    }

    public List<Item> getNormalItems() {
        List<Item> normalItems = new ArrayList<>(); 
        for (Item item : itemList) {
            if (item.getLocation() == ItemLocation.NORMAL) {
                normalItems.add(item); 
            }
        }
        return normalItems;
    }

    public List<Item> getESGItems() {
        List<Item> esgItems = new ArrayList<>(); 
        for (Item item : itemList) {
            if (item.getLocation() == ItemLocation.ESG) {
                esgItems.add(item); 
            }
        }
        return esgItems;
    }

    // --- 아이템 관리 (옵저버 알림 추가) ---
    public void addItem(Item item) {
        itemList.add(item);
        saveItems();
        notifyItemAdded(item);
    }

    public void removeItem(Item item) {
        itemList.remove(item);
        saveItems();
        notifyItemRemoved(item);
    }

    public void updateItem(Item item) {
        saveItems();
        notifyItemUpdated(item);
    }

    private void saveItems() {
        FileHandler.saveItems(ITEM_FILE, itemList);
    }
}