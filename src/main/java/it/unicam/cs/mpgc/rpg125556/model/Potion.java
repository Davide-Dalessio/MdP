package it.unicam.cs.mpgc.rpg125556.model;

public abstract class Potion extends Item {
    protected Potion() {
        super();
    }

    public Potion(String name) {
        super(name);
    }

    public abstract void use(Playable target);
}
