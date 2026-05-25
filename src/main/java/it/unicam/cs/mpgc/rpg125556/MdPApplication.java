package it.unicam.cs.mpgc.rpg125556;

import it.unicam.cs.mpgc.rpg125556.model.BattleManager;
import it.unicam.cs.mpgc.rpg125556.model.Warrior;
import it.unicam.cs.mpgc.rpg125556.model.Zombie;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MdPApplication extends Application {

    private TextArea terminalArea;
    private Label statusLabel;
    private BattleManager battle;

    @Override
    public void start(Stage primaryStage) {

        Warrior warrior = new Warrior("Warrior");
        Zombie zombie = new Zombie();
        battle = new BattleManager(warrior, zombie);
        terminalArea = new TextArea();

        terminalArea.setText("=== COMBATTIMENTO ===\n" + warrior.getName() + " vs " + zombie.getName() + "\n");

        statusLabel = new Label(battle.getStatus());
        statusLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-padding: 5 10;");

        Button attackBtn = new Button("Attacca");
        Button potionBtn = new Button("Usa Pozione");
        Button fleeBtn = new Button("Fuggi");

        attackBtn.setOnAction(e -> {
            battle.playerAttack();
            if (!battle.isBattleOver()) {
                battle.enemyAttack();
            }
            terminalArea.setText(battle.getLog());
            statusLabel.setText(battle.getStatus());
            if (battle.isBattleOver()) {
                attackBtn.setDisable(true);
                potionBtn.setDisable(true);
                fleeBtn.setDisable(true);
            }
        });

        potionBtn.setOnAction(e -> {
            battle.usePotionInBattle();
            terminalArea.setText(battle.getLog());
            statusLabel.setText(battle.getStatus());
        });

        fleeBtn.setOnAction(e -> {
            boolean fled = battle.tryFlee();
            terminalArea.setText(battle.getLog());
            statusLabel.setText(battle.getStatus());
            if (fled || battle.isBattleOver()) {
                attackBtn.setDisable(true);
                potionBtn.setDisable(true);
                fleeBtn.setDisable(true);
            }
        });

        HBox buttonBar = new HBox(10, attackBtn, potionBtn, fleeBtn);
        buttonBar.setPadding(new Insets(10));
        VBox root = new VBox(terminalArea, statusLabel, buttonBar);
        VBox.setVgrow(terminalArea, Priority.ALWAYS);
        primaryStage.setTitle("RPG - Matricola 125556");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
