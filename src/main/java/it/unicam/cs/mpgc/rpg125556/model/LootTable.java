package it.unicam.cs.mpgc.rpg125556.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LootTable {
    private final List<LootEntry> entries;
    private final Random random;

    public LootTable() {
        this.entries = new ArrayList<>();
        this.random = new Random();
    }

    public void addEntry(Item item, int dropRate) {
        entries.add(new LootEntry(item, dropRate));
    }

    public List<Item> roll() {
        return entries.stream()
                .filter(entry -> random.nextInt(100) < entry.getDropRate())
                .map(LootEntry::getItem)
                .collect(Collectors.toList());
    }
}
