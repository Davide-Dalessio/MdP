package it.unicam.cs.mpgc.rpg125556;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import it.unicam.cs.mpgc.rpg125556.model.*;


public class MdPApplication extends Application {

    private TextArea terminalArea;
    private BattleManager battle;

    @Override
    public void start(Stage primaryStage) {

        Warrior warrior = new Warrior("Warrior");
        Zombie zombie = new Zombie(50, "Flesh");
        BattleManager battle = new BattleManager(warrior, zombie);

        terminalArea = new TextArea();
        terminalArea.setEditable(false);

        terminalArea.setText("=== COMBATTIMENTO ===\n" + warrior.getName() + " vs " + zombie.getName() + "\n");
        Button attackBtn = new Button("Attacca");
        Button fleeBtn = new Button("Fuggi");
        attackBtn.setOnAction(e -> {
            battle.playerAttack();
            if (!battle.isBattleOver()) {
                battle.enemyAttack();
            }
            terminalArea.setText(battle.getLog());
            if (battle.isBattleOver()) {
                attackBtn.setDisable(true);
                fleeBtn.setDisable(true);
            }
        });
        fleeBtn.setOnAction(e -> {
            boolean fled = battle.tryFlee();
            terminalArea.setText(battle.getLog());
            if (fled || battle.isBattleOver()) {
                attackBtn.setDisable(true);
                fleeBtn.setDisable(true);
            }
        });
        HBox buttonBar = new HBox(10, attackBtn, fleeBtn);
        buttonBar.setPadding(new Insets(10, 10, 10, 10));
        VBox root = new VBox(terminalArea, buttonBar);
        VBox.setVgrow(terminalArea, Priority.ALWAYS);
        primaryStage.setTitle("RPG - Matricola 125556");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
