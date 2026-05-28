package it.unicam.cs.mpgc.rpg125556.model;

public interface Playable {
    void gainExperience(int amount);
    void levelUp();
    int getExperience();
    int getLevel();
    int getHealth();
    void setHealth(int health);
    int getMaxHealth();
}