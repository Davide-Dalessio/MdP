package it.unicam.cs.mpgc.rpg125556.model;

public abstract class Enemy extends Entity implements NonPlayable {
    private final int experienceReward;
    private final LootTable lootTable;

    public Enemy(String name, int maxHealth, int attack, int defense, int speed, int experienceReward) {
        super(name, maxHealth, attack, defense, speed);
        this.experienceReward = experienceReward;
        this.lootTable = buildLootTable();
    }

    protected abstract LootTable buildLootTable();

    @Override
    public int getExperienceReward() {
        return experienceReward;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }
}
