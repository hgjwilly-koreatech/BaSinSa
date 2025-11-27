package com.inventory.model.PantsType;

public class HalfPants extends PantsState {

    public HalfPants() {
        this.type = PantsType.HALF;
    }

    @Override
    public boolean recycle() {
        this.type =  PantsType.values()[rand.nextInt(2) + 2];
        return true;
    }

    @Override
    public String toString() {
        return "HALF";
    }

    @Override
    public String toName() {
        return "반바지";
    }
}