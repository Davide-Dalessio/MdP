package it.unicam.cs.mpgc.rpg125556.model;

public abstract class Armor extends Item {
    private final int defenseBonus;

    protected Armor() {
        super();
        this.defenseBonus = 0;
    }

    public Armor(String name, int defenseBonus) {
        super(name);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }
}
