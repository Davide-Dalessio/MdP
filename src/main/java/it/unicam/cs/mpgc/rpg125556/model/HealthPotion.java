package it.unicam.cs.mpgc.rpg125556.model;

public class HealthPotion extends Potion {
    private final int healAmount;

    public HealthPotion(String name, int healAmount) {
        super(name);
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    @Override
    public void use(Warrior target) {
        target.setHealth(Math.min(target.getHealth() + healAmount, target.getMaxHealth()));
    }
}
