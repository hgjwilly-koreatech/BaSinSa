
package com.inventory.model;

import com.inventory.manager.ItemManager;
import com.inventory.manager.SalesManager;
import com.inventory.model.PantsType.PantsType;

import javax.swing.*;
import java.time.LocalDateTime;

public class NormalMember extends Member implements IItemManagable {

    public NormalMember(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public String getMemberType() {
        return "Normal";
    }

    @Override
    public void add(JFrame owner) {
        // GUI 팝업을 통해 사용자 입력 받기
        try {
            String typeStr = (String) JOptionPane.showInputDialog(owner, "바지 종류:", "새 바지 입고",
                    JOptionPane.PLAIN_MESSAGE, null, new String[]{"LONG", "SEVENTH", "HALF", "SHORTS"}, "LONG");
            if (typeStr == null) return; // 취소

            String material = JOptionPane.showInputDialog(owner, "재질:");
            if (material == null) return;

            String quality = JOptionPane.showInputDialog(owner, "품질:");
            if (quality == null) return;

            int price = Integer.parseInt(JOptionPane.showInputDialog(owner, "단가:"));

            PantsType type = PantsType.valueOf(typeStr);

            Item newItem = new ItemBuilder()
                    .state(type)
                    .isESG(false)
                    .location(ItemLocation.NORMAL)
                    .material(material)
                    .quality(quality)
                    .price(price).Build();

            ItemManager.getInstance().addItem(newItem);
            JOptionPane.showMessageDialog(owner, "새 바지가 입고되었습니다. (번호: " + newItem.getItemNumber() + ")");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(owner, "단가는 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(owner, "입고 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void move(Item item) {
        // 일반 사원은 일반 재고 -> ESG 재고로 이동
        if (item.getLocation() == ItemLocation.NORMAL) {
            item.setLocation(ItemLocation.ESG);
            ItemManager.getInstance().updateItem(item);
            System.out.println("아이템 이동 (Normal -> ESG): " + item.getItemNumber());
        }
    }

    @Override
    public void outgoing(Item item) {
        // 일반 사원은 일반 재고에서 출고
        if (item.getLocation() == ItemLocation.NORMAL) {
            ItemManager.getInstance().removeItem(item);
            SalesManager.getInstance().recordSale(item.getPrice(), LocalDateTime.now());
            System.out.println("아이템 출고: " + item.getItemNumber() + ", 매출: " + item.getPrice());
        }
    }
}