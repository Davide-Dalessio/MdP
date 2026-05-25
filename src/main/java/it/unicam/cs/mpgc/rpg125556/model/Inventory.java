package it.unicam.cs.mpgc.rpg125556.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Inventory {
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public List<Item> getItemsByType(ItemType type) {
        return items.stream()
                .filter(item -> item.getType() == type)
                .collect(Collectors.toList());
    }

    public Optional<Item> getBestWeapon() {
        return items.stream()
                .filter(item -> item.getType() == ItemType.WEAPON)
                .max(Comparator.comparingInt(Item::getValue));
    }

    public List<Item> getAll() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
