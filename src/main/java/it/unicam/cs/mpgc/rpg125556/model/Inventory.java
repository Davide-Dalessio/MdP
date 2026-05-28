package it.unicam.cs.mpgc.rpg125556.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Inventory {
    private List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public <T extends Item> List<T> getItemsByClass(Class<T> clazz) {
        return items.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Optional<Weapon> getBestWeapon() {
        return getItemsByClass(Weapon.class).stream()
                .max(Comparator.comparingInt(Weapon::getAttackBonus));
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
