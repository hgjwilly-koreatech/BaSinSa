package com.inventory.model;

public class CEO extends Member {

    public CEO(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public String getMemberType() {
        return "CEO";
    }

    // CEO의 기능은 매니저(AccountManager, SalesManager)를 통해 호출되므로
    // 이 클래스 자체에는 별도 메서드가 필요하지 않을 수 있습니다.
    // GUI에서 이 타입(instanceof CEO)을 확인하여 기능을 분기합니다.
}