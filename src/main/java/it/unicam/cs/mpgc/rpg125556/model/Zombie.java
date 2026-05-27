package it.unicam.cs.mpgc.rpg125556.model;

public class Zombie extends Enemy {

    public Zombie() {
        super("Zombie", 200, 30, 10, 10, 50);
    }

    @Override
    protected LootTable buildLootTable() {
        LootTable table = new LootTable();
        table.addEntry(new HealthPotion("Flesh", 20), 80);
        table.addEntry(new HealthPotion("Health Potion", 50), 30);
        return table;
    }
}
