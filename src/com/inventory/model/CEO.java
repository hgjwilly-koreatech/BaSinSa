package com.inventory.model;

public class CEO extends Member {

    public CEO(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public String getMemberType() {
        return "CEO";
    }
}