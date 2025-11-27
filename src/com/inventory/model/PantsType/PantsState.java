package com.inventory.model.PantsType;

import java.util.Random;

public abstract class PantsState {
    protected static final Random rand = new Random();
    protected PantsType type;

    protected PantsState() {
    }

    public PantsType getType() {
        return type;
    }

    public abstract PantsState recycle();

    @Override
    public abstract String toString();
    public abstract String toName();
}