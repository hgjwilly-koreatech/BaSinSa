package com.inventory.model;

/**
 * 재고(Item) 데이터의 변경 사항을 감지하는 옵저버 인터페이스
 */
public interface ItemObserver {
    void onItemAdded(Item item);
    void onItemRemoved(Item item);
    void onItemUpdated(Item item);
}