package it.unicam.cs.mpgc.rpg125556.model;

import java.util.ArrayList;
import java.util.List;

public class BattleManager {
    private final Warrior player;
    private final Zombie enemy;
    private final List<String> battleLog;
    public BattleManager(Warrior player, Zombie enemy) {
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

    public String getLog() {
        return battleLog.stream()
                .collect(java.util.stream.Collectors.joining("\n"));
    }





}