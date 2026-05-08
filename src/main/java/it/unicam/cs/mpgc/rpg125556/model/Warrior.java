package it.unicam.cs.mpgc.rpg125556.model;

public class Warrior extends Entity implements Playable {
    private int experience;
    private int level;
    private int maxExperience;

    public Warrior(String name) {
        super(name, 100, 50, 30, 20);
        this.experience = 0;
        this.level = 1;
        this.maxExperience = 100;
    }

    @Override
    public int getExperience() {
        return experience;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void gainExperience(int amount){
        experience += amount;
        while (experience >= maxExperience){
            experience -= maxExperience;
            this.levelUp();
        }
    }


    @Override
    public void levelUp(){
        this.level++;
        this.maxExperience = (int) (maxExperience * 1.2);
    }
}