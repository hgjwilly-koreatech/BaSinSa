package com.inventory.model.PantsType;

public class HalfPants extends PantsState {

    public HalfPants() {
        this.type = PantsType.HALF;
    }

    @Override
    public PantsState recycle() {
        int randInt = rand.nextInt(100);

        if(randInt < 30)
            return new HalfPants();
        else if(randInt < 90)
            return new ShortPants();
        else
            return new SeventhPants();
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