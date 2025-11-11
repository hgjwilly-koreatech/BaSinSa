package com.inventory.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 바지(Item) 클래스
 */
public class Item {
    private String itemNumber;  // 물품 번호 (자동 지정)
    private PantsType type;     // 바지 종류
    private boolean isESG;      // ESG 활용 여부
    private String material;    // 재질
    private String quality;     // 품질
    private int price;          // 단가
    private ItemLocation location; // 재고 위치
    private LocalDateTime entryDate; // 입고 날짜/시간

    // 파일 저장을 위한 포맷
    private static final DateTimeFormatter TxtFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // 생성자 (새 물품 입고 시)
    public Item(PantsType type, String material, String quality, int price) {
        this.itemNumber = UUID.randomUUID().toString().substring(0, 8); // 랜덤 물품 번호
        this.type = type;
        this.material = material;
        this.quality = quality;
        this.price = price;
        this.isESG = false; // 일반사원 입고 시 기본 false
        this.location = ItemLocation.NORMAL; // 일반사원 입고 시 기본 일반재고
        this.entryDate = LocalDateTime.now(); // 현재 시간 자동 입력
    }

    // 파일에서 불러올 때 사용하는 생성자 (모든 필드)
    public Item(String itemNumber, PantsType type, boolean isESG, String material,
                String quality, int price, ItemLocation location, LocalDateTime entryDate) {
        this.itemNumber = itemNumber;
        this.type = type;
        this.isESG = isESG;
        this.material = material;
        this.quality = quality;
        this.price = price;
        this.location = location;
        this.entryDate = entryDate;
    }

    // Getters and Setters
    public String getItemNumber() { return itemNumber; }
    public PantsType getType() { return type; }
    public boolean isESG() { return isESG; }
    public String getMaterial() { return material; }
    public String getQuality() { return quality; }
    public int getPrice() { return price; }
    public ItemLocation getLocation() { return location; }
    public LocalDateTime getEntryDate() { return entryDate; }

    public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }
    public void setType(PantsType type) { this.type = type; }
    public void setESG(boolean isESG) { this.isESG = isESG; }
    public void setMaterial(String material) { this.material = material; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setPrice(int price) { this.price = price; }
    public void setLocation(ItemLocation location) { this.location = location; }

    // txt 파일 저장을 위한 문자열 변환
    public String toFileString() {
        // 구분자(delimiter)로 콤마(,) 사용
        return String.join(",",
                itemNumber,
                type.name(),
                String.valueOf(isESG),
                material,
                quality,
                String.valueOf(price),
                location.name(),
                entryDate.format(TxtFormat)
        );
    }

    // txt 파일 로드를 위한 문자열 파싱
    public static Item fromFileString(String line) {
        try {
            String[] parts = line.split(",");
            return new Item(
                    parts[0],
                    PantsType.valueOf(parts[1]),
                    Boolean.parseBoolean(parts[2]),
                    parts[3],
                    parts[4],
                    Integer.parseInt(parts[5]),
                    ItemLocation.valueOf(parts[6]),
                    LocalDateTime.parse(parts[7], TxtFormat)
            );
        } catch (Exception e) {
            System.err.println("아이템 파일 파싱 오류: " + line);
            return null;
        }
    }

    @Override
    public String toString() {
        return type.toString() + " (" + itemNumber + ")";
    }
}