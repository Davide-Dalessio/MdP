package it.unicam.cs.mpgc.rpg125556.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Entity {
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attack;
    protected int defense;
    protected int speed;

    protected Entity() {
        this.name = "";
    }

    public Entity(String name, int maxHealth, int attack, int defense, int speed) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
        }
    }

    @JsonIgnore
    public boolean isDead() {
        return this.health <= 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
