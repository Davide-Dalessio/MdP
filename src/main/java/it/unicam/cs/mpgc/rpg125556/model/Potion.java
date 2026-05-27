package it.unicam.cs.mpgc.rpg125556.model;

public abstract class Potion extends Item {
    public Potion(String name) {
        super(name);
    }

    public abstract void use(Warrior target);
}
