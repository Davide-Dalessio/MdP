package it.unicam.cs.mpgc.rpg125556.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Optional;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Warrior.class, name = "warrior"),
    @JsonSubTypes.Type(value = Mage.class, name = "mage")
})
public abstract class Hero extends Entity implements Playable {
    protected int experience;
    protected int level;
    protected int maxExperience;
    protected Inventory inventory;

    protected Hero() {
        super();
        this.inventory = new Inventory();
    }

    public Hero(String name, int maxHealth, int attack, int defense, int speed) {
        super(name, maxHealth, attack, defense, speed);
        this.experience = 0;
        this.level = 1;
        this.maxExperience = 100;
        this.inventory = new Inventory();
    }

    @Override
    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(int maxExperience) {
        this.maxExperience = maxExperience;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void gainExperience(int amount) {
        experience += amount;
        while (experience >= maxExperience) {
            experience -= maxExperience;
            this.levelUp();
        }
    }

    @Override
    public void levelUp() {
        this.level++;
        this.maxExperience = (int) (maxExperience * 1.2);
    }

    public Optional<Potion> usePotion() {
        Optional<Potion> potion = inventory.getItemsByClass(Potion.class)
                .stream()
                .findFirst();
        potion.ifPresent(p -> {
            p.use(this);
            inventory.removeItem(p);
        });
        return potion;
    }
}
