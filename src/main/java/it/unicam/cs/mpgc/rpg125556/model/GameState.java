package it.unicam.cs.mpgc.rpg125556.model;

import java.util.List;

public class GameState {
    private Hero player;
    private Enemy enemy;
    private List<String> battleLog;

    public GameState() {}

    public GameState(Hero player, Enemy enemy, List<String> battleLog) {
        this.player = player;
        this.enemy = enemy;
        this.battleLog = battleLog;
    }

    public Hero getPlayer() {
        return player;
    }

    public void setPlayer(Hero player) {
        this.player = player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public List<String> getBattleLog() {
        return battleLog;
    }

    public void setBattleLog(List<String> battleLog) {
        this.battleLog = battleLog;
    }
}
