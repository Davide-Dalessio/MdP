package it.unicam.cs.mpgc.rpg125556.model;

import java.util.ArrayList;
import java.util.List;

public class BattleManager {
    private final Warrior player;
    private final Enemy enemy;
    private final List<String> battleLog;

    public BattleManager(Warrior player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.battleLog = new ArrayList<>();
    }

    public boolean isBattleOver() {
        return player.isDead() || enemy.isDead();
    }

    private int performAttack(Entity attacker, Entity defender) {
        int damage = Math.max(1, attacker.getAttack() - defender.getDefense());
        defender.takeDamage(damage);
        battleLog.add(attacker.getName() + " attacca per " + damage + " danni");
        return damage;
    }

    public void playerAttack() {
        performAttack(player, enemy);
        if (enemy.isDead()) {
            battleLog.add(enemy.getName() + " è stato sconfitto.");
            player.gainExperience(enemy.getExperienceReward());
            battleLog.add("+" + enemy.getExperienceReward() + " XP guadagnati.");
            List<Item> drops = enemy.getLootTable().roll();
            if (drops.isEmpty()) {
                battleLog.add("Nessun loot trovato.");
            } else {
                drops.forEach(item -> {
                    player.getInventory().addItem(item);
                    battleLog.add("Trovato: " + item.getName() + " aggiunto all'inventario.");
                });
            }
        }
    }

    public void enemyAttack() {
        performAttack(enemy, player);
        if (player.isDead()) {
            battleLog.add(player.getName() + " è stato sconfitto.");
        }
    }

    public boolean tryFlee() {
        int fleeChance = (int) ((double) player.getSpeed() / (player.getSpeed() + enemy.getSpeed()) * 100);
        boolean success = new java.util.Random().nextInt(100) < fleeChance;

        if (success) {
            battleLog.add(player.getName() + " è fuggito con successo");
        } else {
            battleLog.add(player.getName() + " ha provato a fuggire ma ha fallito.");
        }
        return success;
    }

    public void usePotionInBattle() {
        player.usePotion().ifPresentOrElse(
                p -> {
                    String logMessage = player.getName() + " usa " + p.getName();
                    if (p instanceof HealthPotion hp) {
                        logMessage += " e recupera " + hp.getHealAmount() + " HP.";
                    } else {
                        logMessage += ".";
                    }
                    battleLog.add(logMessage);
                },
                () -> battleLog.add("Nessuna pozione nell'inventario!")
        );
    }

    public String getStatus() {
        return player.getName() + " HP: " + player.getHealth() + "/" + player.getMaxHealth() +
               "   |   " +
               enemy.getName() + " HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth();
    }

    public String getLog() {
        return String.join("\n", battleLog);
    }
}