package com.inventory.manager;

import com.inventory.model.Member;

import java.util.List;

public class LoginManager {
    private static final LoginManager instance = new LoginManager();
    private List<Member> memberList;

    private LoginManager() {
        // AccountManager에서 사원 리스트를 가져옴
        this.memberList = AccountManager.getInstance().getMemberList();
    }

    public static LoginManager getInstance() {
        return instance;
    }

    /**
     * 로그인을 시도
     * @return 성공 시 Member 객체, 실패 시 null
     */
    public Member login(String id, String password) {
        for (Member member : memberList) {
            if (member.getId().equals(id) && member.getPassword().equals(password)) {
                return member; // 로그인 성공
            }
        }
        return null; // 로그인 실패
    }
}