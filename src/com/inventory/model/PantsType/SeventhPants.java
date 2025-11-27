package com.inventory.model.PantsType;


public class SeventhPants extends PantsState {

    public SeventhPants() {
        this.type = PantsType.SEVENTH;
    }

    @Override
    public PantsState recycle() {
        int randNum = rand.nextInt(100);

        if(randNum < 20)
        {
            return new  SeventhPants();
        }
        else if(randNum < 50)
        {
            return new HalfPants();
        }
        else if(randNum < 95)
            return new ShortPants();
        else {
            return new LongPants();
        }
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