package com.inventory.model.PantsType;

public class LongPants extends PantsState {

    public LongPants() {
        this.type = PantsType.LONG;
    }

    @Override
    public PantsState recycle() {
        int randNum = rand.nextInt(100);

        if(randNum < 10) {
            return new  LongPants();
        }
        else if(randNum < 30) {
            return new SeventhPants();
        }
        else if(randNum < 70) {
            return new HalfPants();
        }
        else
            return new ShortPants();
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