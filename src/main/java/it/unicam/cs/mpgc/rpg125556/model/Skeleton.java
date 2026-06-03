package it.unicam.cs.mpgc.rpg125556.model;

public class Skeleton extends Enemy {

    public Skeleton() {
        super("Scheletro", 120, 45, 5, 25, 60);
    }

    @Override
    protected LootTable buildLootTable() {
        LootTable table = new LootTable();
        table.addEntry(new HealthPotion("Health Potion", 50), 40);
        table.addEntry(new Sword("Spada di Ferro", 15), 15);
        table.addEntry(new Shield("Scudo di Legno", 10), 15);
        return table;
    }
}
