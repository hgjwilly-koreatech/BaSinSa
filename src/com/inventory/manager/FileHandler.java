package com.inventory.manager;

import com.inventory.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileHandler {
    public static List<Member> loadMembers(String filename) {
        List<Member> members = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return members;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                String type = parts[0];
                String id = parts[1];
                String pw = parts[2];
                String name = parts[3];

                switch (type) {
                    case "CEO":
                        members.add(new CEO(id, pw, name));
                        break;
                    case "Normal":
                        members.add(new NormalMember(id, pw, name));
                        break;
                    case "ESG":
                        members.add(new ESGMember(id, pw, name));
                        break;
                }
            }
        } catch (Exception e) {
            System.out.print("끼야악00 - ");
            System.out.println(e.getMessage());
        }
        return members;
    }

    public static void saveMembers(String filename, List<Member> members) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Member member : members) {
                bw.write(member.toFileString());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.print("끼야악00 - ");
            System.out.println(e.getMessage());
        }
    }

    public static List<Item> loadItems(String filename) {
        List<Item> items = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return items;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Item item = Item.fromFileString(line);
                if (item != null) {
                    items.add(item);
                }
            }
        } catch (Exception e) {
            System.out.print("끼야악00 - ");
            System.out.println(e.getMessage());
        }
        return items;
    }

    public static void saveItems(String filename, List<Item> items) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Item item : items) {
                bw.write(item.toFileString());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.print("끼야악00 - ");
            System.out.println(e.getMessage());
        }
    }

    public static Map<LocalDate, Integer> loadSales(String filename) {
        Map<LocalDate, Integer> sales = new HashMap<>();
        File file = new File(filename);
        if (!file.exists()) return sales;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                LocalDate date = LocalDate.parse(parts[0]);
                int amount = Integer.parseInt(parts[1]);
                sales.put(date, sales.getOrDefault(date, 0) + amount);
            }
        } catch (Exception e) {
            System.out.print("끼야악00 - ");
            System.out.println(e.getMessage());
        }
        return sales;
    }

    public static void saveSales(String filename, Map<LocalDate, Integer> sales) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (var entry : sales.entrySet()) {
                bw.write(entry.getKey().toString() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.print("끼야악00 - ");
            System.out.println(e.getMessage());
        }
    }
}