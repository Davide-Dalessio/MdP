package it.unicam.cs.mpgc.rpg125556.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    protected int statPoints;
    protected Weapon equippedWeapon;
    protected Armor equippedArmor;

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
        this.statPoints = 0;
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
        this.statPoints += 5;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = statPoints;
    }

    public void allocatePointToMaxHealth() {
        if (statPoints > 0) {
            this.maxHealth += 10;
            this.health += 10;
            this.statPoints--;
        }
    }

    public void allocatePointToAttack() {
        if (statPoints > 0) {
            this.attack += 5;
            this.statPoints--;
        }
    }

    public void allocatePointToDefense() {
        if (statPoints > 0) {
            this.defense += 3;
            this.statPoints--;
        }
    }

    public void allocatePointToSpeed() {
        if (statPoints > 0) {
            this.speed += 2;
            this.statPoints--;
        }
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

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedArmor(Armor equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    @JsonIgnore
    @Override
    public int getAttack() {
        int baseAttack = super.getAttack();
        if (equippedWeapon != null) {
            baseAttack += equippedWeapon.getAttackBonus();
        }
        return baseAttack;
    }

    @JsonProperty("attack")
    public int getBaseAttack() {
        return super.getAttack();
    }

    @JsonProperty("attack")
    public void setBaseAttack(int baseAttack) {
        this.setAttack(baseAttack);
    }

    @JsonIgnore
    @Override
    public int getDefense() {
        int baseDefense = super.getDefense();
        if (equippedArmor != null) {
            baseDefense += equippedArmor.getDefenseBonus();
        }
        return baseDefense;
    }

    @JsonProperty("defense")
    public int getBaseDefense() {
        return super.getDefense();
    }

    @JsonProperty("defense")
    public void setBaseDefense(int baseDefense) {
        this.setDefense(baseDefense);
    }

    public void equipWeapon(Weapon w) {
        if (this.equippedWeapon != null) {
            this.inventory.addItem(this.equippedWeapon);
        }
        this.inventory.removeItem(w);
        this.equippedWeapon = w;
    }

    public void unequipWeapon() {
        if (this.equippedWeapon != null) {
            this.inventory.addItem(this.equippedWeapon);
            this.equippedWeapon = null;
        }
    }

    public void equipArmor(Armor a) {
        if (this.equippedArmor != null) {
            this.inventory.addItem(this.equippedArmor);
        }
        this.inventory.removeItem(a);
        this.equippedArmor = a;
    }

    public void unequipArmor() {
        if (this.equippedArmor != null) {
            this.inventory.addItem(this.equippedArmor);
            this.equippedArmor = null;
        }
    }
}
