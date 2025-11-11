package com.inventory.model;

import javax.swing.JFrame;

/**
 * 아이템 관리 기능 인터페이스 (일반사원, ESG사원)
 */
public interface IItemManagable {
    // 새 아이템 추가
    void add(JFrame owner);

    // 아이템 이동 (Normal -> ESG or ESG -> Normal)
    void move(Item item);

    // 아이템 출고 (NormalMember만 해당)
    void outgoing(Item item);
}