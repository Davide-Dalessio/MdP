package it.unicam.cs.mpgc.rpg125556.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Sword.class, name = "sword"),
    @JsonSubTypes.Type(value = Shield.class, name = "shield"),
    @JsonSubTypes.Type(value = HealthPotion.class, name = "health_potion")
})
public abstract class Item {
    private final String name;

    protected Item() {
        this.name = "";
    }

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
