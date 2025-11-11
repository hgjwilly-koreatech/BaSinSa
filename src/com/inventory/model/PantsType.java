package com.inventory.model;

/**
 * 바지 종류를 나타내는 열거형 (Enum)
 */
public enum PantsType {
    LONG("긴바지"),
    SEVENTH("칠부바지"),
    HALF("반바지"),
    SHORTS("숏팬츠");

    private final String displayName;

    PantsType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}