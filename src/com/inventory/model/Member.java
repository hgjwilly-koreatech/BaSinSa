package com.inventory.model;

/**
 * 모든 사원의 부모 클래스
 */
public abstract class Member {
    protected String id;
    protected String password;
    protected String name;

    public Member(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getName() { return name; }

    /**
     * 파일 저장을 위해 멤버 타입을 반환
     */
    public abstract String getMemberType();

    // 파일 저장을 위한 문자열 변환
    public String toFileString() {
        return String.join(",", getMemberType(), id, password, name);
    }
}