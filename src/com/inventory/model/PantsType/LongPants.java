package com.inventory.model.PantsType;

public class LongPants extends PantsState {

    public LongPants() {
        this.type = PantsType.LONG;
    }

    @Override
    public boolean recycle() {
        type = PantsType.values()[rand.nextInt(4)];
        return true;
    }

    @Override
    public String toString() {
        return "LONG";
    }

    @Override
    public String toName() {
        return "긴바지";
    }
}