package com.inventory.manager;

import com.inventory.model.CEO;
import com.inventory.model.ESGMember;
import com.inventory.model.Member;
import com.inventory.model.NormalMember;

import java.util.List;
import java.util.Optional;

public class AccountManager {
    private static final AccountManager instance = new AccountManager();
    private static final String MEMBER_FILE = "members.txt";
    private List<Member> memberList;

    private AccountManager() {
        memberList = FileHandler.loadMembers(MEMBER_FILE);

        // 파일이 비어있을 경우, 초기 CEO 계정 생성
        if (memberList.isEmpty()) {
            System.out.println("초기 CEO 계정(admin/1234)을 생성합니다.");
            memberList.add(new CEO("admin", "1234", "관리자"));
            saveMembers();
        }
    }

    public static AccountManager getInstance() {
        return instance;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void addMember(String type, String id, String password, String name) {
        // ID 중복 체크
        if (memberList.stream().anyMatch(m -> m.getId().equals(id))) {
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }

        Member newMember;
        switch (type) {
            case "Normal":
                newMember = new NormalMember(id, password, name);
                break;
            case "ESG":
                newMember = new ESGMember(id, password, name);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 사원 유형입니다.");
        }
        memberList.add(newMember);
        saveMembers();
    }

    public void removeMember(String id) {
        memberList.removeIf(member -> member.getId().equals(id) && !(member instanceof CEO)); // CEO는 삭제 방지
        saveMembers();
    }

    public Optional<Member> findMember(String id) {
        return memberList.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    private void saveMembers() {
        FileHandler.saveMembers(MEMBER_FILE, memberList);
    }
}