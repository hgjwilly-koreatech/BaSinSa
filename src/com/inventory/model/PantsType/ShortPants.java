package com.inventory.model.PantsType;

public class ShortPants extends PantsState {

    public ShortPants() {
        this.type = PantsType.SHORTS;
    }

    @Override
    public boolean recycle() {
        return false;
    }

    @Override
    public String toString() {
        return "SHORTS";
    }

    @Override
    public String toName() {
        return "숏팬츠";
    }
}