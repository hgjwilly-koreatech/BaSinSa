package com.inventory.model;

import com.inventory.model.PantsType.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Item {
    private String itemNumber;  // 물품 번호 (자동 지정)
    private PantsState state;     // 바지 종류
    private boolean isESG;      // ESG 활용 여부
    private String material;    // 재질
    private String quality;     // 품질
    private int price;          // 단가
    private ItemLocation location; // 재고 위치
    private LocalDateTime entryDate; // 입고 날짜/시간

    // 파일 저장을 위한 포맷
    private static final DateTimeFormatter TxtFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // 모든 필드 생성자
    public Item(String itemNumber, PantsState type, boolean isESG, String material,
                String quality, int price, ItemLocation location, LocalDateTime entryDate) {
        this.itemNumber = itemNumber;
        this.state = type;
        this.isESG = isESG;
        this.material = material;
        this.quality = quality;
        this.price = price;
        this.location = location;
        this.entryDate = entryDate;
    }

    // Getters and Setters
    public String getItemNumber() { return itemNumber; }
    public PantsState getPantsState() { return state; }
    public boolean isESG() { return isESG; }
    public String getMaterial() { return material; }
    public String getQuality() { return quality; }
    public int getPrice() { return price; }
    public ItemLocation getLocation() { return location; }
    public LocalDateTime getEntryDate() { return entryDate; }

    public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }
    public void setState(PantsState state) { this.state = state; }
    public void setESG(boolean isESG) { this.isESG = isESG; }
    public void setMaterial(String material) { this.material = material; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setPrice(int price) { this.price = price; }
    public void setLocation(ItemLocation location) { this.location = location; }

    public String toFileString() {
        return String.join(",",
                itemNumber,
                state.toString(),
                String.valueOf(isESG),
                material,
                quality,
                String.valueOf(price),
                location.name(),
                entryDate.format(TxtFormat)
        );
    }

    public static Item fromFileString(String line) {
        try {
            String[] parts = line.split(",");
            return new Item(
                    parts[0],
                    stringToPantsState(parts[1]),
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
        return state.toString() + " (" + itemNumber + ")";
    }

    private static PantsState stringToPantsState(String s)
    {
        PantsType type = PantsType.valueOf(s);
        switch(type)
        {
            case LONG:
                return new LongPants();
            case HALF:
                return new HalfPants();
            case SHORTS:
                return new ShortPants();
            case SEVENTH:
                return new SeventhPants();
        }
        return new LongPants();
    }
}