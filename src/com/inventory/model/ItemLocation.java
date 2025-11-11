package com.inventory.model;

/**
 * 재고 위치 (일반/ESG)
 */
public enum ItemLocation {
    NORMAL("일반재고"),
    ESG("ESG재고");

    private final String displayName;

    ItemLocation(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}