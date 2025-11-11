package com.inventory.model;

import com.inventory.manager.ItemManager;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class ESGMember extends Member implements IItemManagable {

    private static final Random rand = new Random();

    public ESGMember(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public String getMemberType() {
        return "ESG";
    }

    @Override
    public void add(JFrame owner) {
        // 헌옷 수거함에서 가져오는 컨셉 (모든 속성 랜덤)
        PantsType randType = PantsType.values()[rand.nextInt(PantsType.values().length)];
        String randMaterial = new String[]{"데님", "면", "폴리에스터", "혼방"}[rand.nextInt(4)];
        String randQuality = new String[]{"상", "중", "하"}[rand.nextInt(3)];
        int randPrice = (rand.nextInt(5) + 1) * 1000; // 1000 ~ 5000
        String randItemNum = UUID.randomUUID().toString().substring(0, 8);

        Item newItem = new Item(
                randItemNum,
                randType,
                false, // 헌옷이므로 ESG 활용 여부는 false
                randMaterial,
                randQuality,
                randPrice,
                ItemLocation.ESG, // ESG 재고로 바로 추가
                LocalDateTime.now()
        );

        ItemManager.getInstance().addItem(newItem);
        JOptionPane.showMessageDialog(owner, "ESG 재고(헌옷)가 추가되었습니다. (번호: " + newItem.getItemNumber() + ")");
    }

    @Override
    public void move(Item item) {
        // ESG 사원은 ESG 재고 -> 일반 재고로 이동 (ESG 활용 여부가 True여야 함)
        if (item.getLocation() == ItemLocation.ESG && item.isESG()) {
            item.setLocation(ItemLocation.NORMAL);
            ItemManager.getInstance().updateItem(item);
            System.out.println("아이템 이동 (ESG -> Normal): " + item.getItemNumber());
        } else if (item.getLocation() == ItemLocation.ESG && !item.isESG()) {
            JOptionPane.showMessageDialog(null, "ESG 바지로 변경(재활용)이 완료되지 않은 상품입니다.", "이동 불가", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void outgoing(Item item) {
        // ESG 사원은 출고 기능이 없음 (요구사항에 따라)
        System.out.println("ESG 사원은 출고 기능이 없습니다.");
    }

    /**
     * ESG 재고를 ESG 바지로 변경 (재활용)
     */
    public void recycle(Item item) {
        if (item.getLocation() != ItemLocation.ESG) return;
        if (item.isESG()) {
            JOptionPane.showMessageDialog(null, "이미 재활용된 상품입니다.", "재활용 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (item.getType() == PantsType.SHORTS) {
            JOptionPane.showMessageDialog(null, "숏팬츠는 재활용이 불가능합니다.", "재활용 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 재질은 그대로, ESG 플래그 true로 변경
        item.setESG(true);

        // 품질, 단가, 물품번호 변경 (랜덤)
        item.setQuality(new String[]{"A급", "B급", "C급"}[rand.nextInt(3)]);
        item.setPrice((rand.nextInt(10) + 5) * 1000); // 5000 ~ 15000
        item.setItemNumber(UUID.randomUUID().toString().substring(0, 8)); // 새 물품 번호

        // 바지 종류 변경 로직
        switch (item.getType()) {
            case LONG: // 긴바지 -> 긴바지, 칠부, 반바지, 숏팬츠
                item.setType(PantsType.values()[rand.nextInt(4)]);
                break;
            case SEVENTH: // 칠부 -> 칠부, 반바지, 숏팬츠
                item.setType(PantsType.values()[rand.nextInt(3) + 1]); // SEVENTH, HALF, SHORTS
                break;
            case HALF: // 반바지 -> 반바지, 숏팬츠
                item.setType(PantsType.values()[rand.nextInt(2) + 2]); // HALF, SHORTS
                break;
        }

        ItemManager.getInstance().updateItem(item);
        JOptionPane.showMessageDialog(null, "재활용 완료. 새 물품번호: " + item.getItemNumber(), "재활용 성공", JOptionPane.INFORMATION_MESSAGE);
    }
}