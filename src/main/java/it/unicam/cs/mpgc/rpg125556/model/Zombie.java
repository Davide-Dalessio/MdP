package it.unicam.cs.mpgc.rpg125556.model;

public class Zombie extends Entity implements NonPlayable{
    private final int experienceReward;
    private final String loot;

    public Zombie(int experienceReward, String loot ) {
        super("Zombie", 200,10,10,10);
        this.experienceReward = 50;
        this.loot = "Flesh";
    }


    @Override
    public int getExperienceReward() {
        return experienceReward;
    }

    @Override
    public String getLoot() {
        return loot;
    }
}
