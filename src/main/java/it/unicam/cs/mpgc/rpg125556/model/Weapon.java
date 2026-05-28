package it.unicam.cs.mpgc.rpg125556.model;

public abstract class Weapon extends Item {
    private final int attackBonus;

    protected Weapon() {
        super();
        this.attackBonus = 0;
    }

    public Weapon(String name, int attackBonus) {
        super(name);
        this.attackBonus = attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}
