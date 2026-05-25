package it.unicam.cs.mpgc.rpg125556.model;

public class LootEntry {
    private final Item item;
    private final int dropRate;

    public LootEntry(Item item, int dropRate) {
        this.item = item;
        this.dropRate = dropRate;
    }

    public Item getItem() {
        return item;
    }

    public int getDropRate() {
        return dropRate;
    }
}
