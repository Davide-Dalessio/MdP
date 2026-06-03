package it.unicam.cs.mpgc.rpg125556.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Zombie.class, name = "zombie"),
    @JsonSubTypes.Type(value = Skeleton.class, name = "skeleton")
})
public abstract class Enemy extends Entity implements NonPlayable {
    private int experienceReward;
    private LootTable lootTable;

    protected Enemy() {
        super();
    }

    public Enemy(String name, int maxHealth, int attack, int defense, int speed, int experienceReward) {
        super(name, maxHealth, attack, defense, speed);
        this.experienceReward = experienceReward;
        this.lootTable = buildLootTable();
    }

    protected abstract LootTable buildLootTable();

    @Override
    public int getExperienceReward() {
        return experienceReward;
    }

    @Override
    @JsonIgnore
    public LootTable getLootTable() {
        return lootTable;
    }

    public static Enemy getRandomEnemy() {
        if (new java.util.Random().nextBoolean()) {
            return new Zombie();
        } else {
            return new Skeleton();
        }
    }
}
