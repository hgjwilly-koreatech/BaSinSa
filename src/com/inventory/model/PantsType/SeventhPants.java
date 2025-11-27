package com.inventory.model.PantsType;


public class SeventhPants extends PantsState {

    public SeventhPants() {
        this.type = PantsType.SEVENTH;
    }

    @Override
    public boolean recycle() {
        this.type = PantsType.values()[rand.nextInt(3) + 1];
        return true;
    }

    @Override
    public String toString() {
        return "SEVENTH";
    }

    @Override
    public String toName() {
        return "칠부바지";
    }
}