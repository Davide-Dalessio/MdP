package it.unicam.cs.mpgc.rpg125556.model;

public class HealthPotion extends Potion {
    private int healAmount;

    private HealthPotion() {
        super();
    }

    public HealthPotion(String name, int healAmount) {
        super(name);
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    @Override
    public void use(Playable target) {
        target.setHealth(Math.min(target.getHealth() + healAmount, target.getMaxHealth()));
    }
}
