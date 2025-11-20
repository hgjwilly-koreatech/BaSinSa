package com.inventory.model;

import java.util.Random;
import java.time.LocalDateTime;
import java.util.UUID;

public class ItemBuilder {
    private String itemNumber;  // 물품 번호 (자동 지정)
    private PantsType type;     // 바지 종류
    private boolean isESG;      // ESG 활용 여부
    private String material;    // 재질
    private String quality;     // 품질
    private int price;          // 단가
    private ItemLocation location; // 재고 위치
    private LocalDateTime entryDate; // 입고 날짜/시간

    private static final Random rand = new Random();

    public ItemBuilder type(PantsType type) {
        this.type = type;
        return this;
    }

    public ItemBuilder type(String type) {
        this.type = PantsType.valueOf(type);
        return this;
    }

    public ItemBuilder isESG(boolean isESG) {
        this.isESG = isESG;
        return this;
    }

    public ItemBuilder material(String material) {
        this.material = material;
        return this;
    }

    public ItemBuilder quality(String quality) {
        this.quality = quality;
        return this;
    }

    public ItemBuilder price(int price) {
        this.price = price;
        return this;
    }

    public ItemBuilder location(ItemLocation location) {
        this.location = location;
        return this;
    }

    public Item Build(){
        this.itemNumber = UUID.randomUUID().toString().substring(0, 8);
        this.entryDate = LocalDateTime.now();
        return new Item(itemNumber, type, isESG, material, quality, price, location, entryDate);
    }

    public Item BuildRandom()
    {
        type = PantsType.values()[rand.nextInt(PantsType.values().length)];
        material = new String[]{"데님", "면", "폴리에스터", "혼방"}[rand.nextInt(4)];
        quality = new String[]{"상", "중", "하"}[rand.nextInt(3)];
        price = (rand.nextInt(5) + 1) * 1000; // 1000 ~ 5000
        itemNumber = UUID.randomUUID().toString().substring(0, 8);

        return new Item(itemNumber, type,false, material, quality, price,
                ItemLocation.ESG, LocalDateTime.now());

    }
}
