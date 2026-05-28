package it.unicam.cs.mpgc.rpg125556;

import it.unicam.cs.mpgc.rpg125556.model.BattleManager;
import it.unicam.cs.mpgc.rpg125556.model.Warrior;
import it.unicam.cs.mpgc.rpg125556.model.Zombie;
import it.unicam.cs.mpgc.rpg125556.model.GameSaveManager;
import it.unicam.cs.mpgc.rpg125556.model.GameState;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
        terminalArea.setEditable(false);
        terminalArea.setStyle(
                "-fx-control-inner-background: black; " +
                "-fx-text-fill: #00FF00; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 14px;"
        );
        terminalArea.setText("=== COMBATTIMENTO ===\n" + warrior.getName() + " vs " + zombie.getName() + "\n");

        statusLabel = new Label(battle.getStatus());
        statusLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-padding: 5 10;");

        Button attackBtn = new Button("Attacca");
        Button potionBtn = new Button("Usa Pozione");
        Button fleeBtn = new Button("Fuggi");
        Button saveBtn = new Button("Salva Gioco");
        Button loadBtn = new Button("Carica Gioco");

        GameSaveManager saveManager = new GameSaveManager();

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

        saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva la partita");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON (*.json)", "*.json"));
            
            // Genera un nome file di default basato sul nome del giocatore e data/ora correnti
            String playerName = battle.getPlayer().getName().replaceAll("\\s+", "_").toLowerCase();
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String defaultName = "save_" + playerName + "_" + now.format(formatter) + ".json";
            fileChooser.setInitialFileName(defaultName);
            
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    GameState state = new GameState(battle.getPlayer(), battle.getEnemy(), battle.getBattleLogList());
                    saveManager.saveGame(file, state);
                    terminalArea.appendText("\n--- GIOCO SALVATO CON SUCCESSO ---\n");
                } catch (Exception ex) {
                    terminalArea.appendText("\nErrore durante il salvataggio: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        loadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Carica una partita");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON (*.json)", "*.json"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    GameState state = saveManager.loadGame(file);
                    
                    battle = new BattleManager(state.getPlayer(), state.getEnemy());
                    battle.setBattleLog(state.getBattleLog());
                    
                    terminalArea.setText(battle.getLog() + "\n--- PARTITA CARICATA ---\n");
                    statusLabel.setText(battle.getStatus());
                    
                    attackBtn.setDisable(false);
                    potionBtn.setDisable(false);
                    fleeBtn.setDisable(false);
                } catch (Exception ex) {
                    terminalArea.appendText("\nErrore durante il caricamento: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        HBox buttonBar = new HBox(10, attackBtn, potionBtn, fleeBtn, saveBtn, loadBtn);
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
