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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MdPApplication extends Application {

    private TextArea terminalArea;
    private Label playerStatusLabel;
    private Label enemyStatusLabel;
    private BattleManager battle;
    private Stage inventoryStage;
    private VBox inventoryItemsList;

    private void updateStatus() {
        playerStatusLabel.setText(battle.getPlayer().getName() + " (LV " + battle.getPlayer().getLevel() + ") HP: " + battle.getPlayer().getHealth() + "/" + battle.getPlayer().getMaxHealth() + " | EXP: " + battle.getPlayer().getExperience() + "/" + battle.getPlayer().getMaxExperience());
        enemyStatusLabel.setText(battle.getEnemy().getName() + " HP: " + battle.getEnemy().getHealth() + "/" + battle.getEnemy().getMaxHealth());
    }

    private void populateInventory(Button attackBtn, Button inventoryBtn, Button fleeBtn) {
        if (inventoryItemsList == null) {
            return;
        }
        inventoryItemsList.getChildren().clear();

        java.util.List<it.unicam.cs.mpgc.rpg125556.model.Potion> potions = battle.getPlayer().getInventory().getItemsByClass(it.unicam.cs.mpgc.rpg125556.model.Potion.class);

        if (potions.isEmpty()) {
            Label emptyLabel = new Label("Nessun oggetto usabile disponibile.");
            emptyLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
            inventoryItemsList.getChildren().add(emptyLabel);
        } else {
            for (it.unicam.cs.mpgc.rpg125556.model.Potion potion : potions) {
                HBox row = new HBox(10);
                row.setStyle("-fx-background-color: #1c1c1f; -fx-padding: 8 12; -fx-background-radius: 5;");
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                String desc = potion.getName();
                if (potion instanceof it.unicam.cs.mpgc.rpg125556.model.HealthPotion hp) {
                    desc += " (Cura: " + hp.getHealAmount() + " HP)";
                }

                Label nameLabel = new Label(desc);
                nameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button useBtn = new Button("Usa");
                useBtn.setStyle(
                        "-fx-background-color: #00FF00; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 4 10; " +
                        "-fx-background-radius: 3; " +
                        "-fx-cursor: hand;"
                );

                useBtn.setOnAction(evt -> {
                    potion.use(battle.getPlayer());
                    battle.getPlayer().getInventory().removeItem(potion);

                    String logMessage = battle.getPlayer().getName() + " usa " + potion.getName();
                    if (potion instanceof it.unicam.cs.mpgc.rpg125556.model.HealthPotion hp) {
                        logMessage += " e recupera " + hp.getHealAmount() + " HP.";
                    } else {
                        logMessage += ".";
                    }
                    battle.getBattleLogList().add(logMessage);

                    if (!battle.isBattleOver()) {
                        battle.enemyAttack();
                    }

                    terminalArea.setText(battle.getLog());
                    updateStatus();

                    if (battle.isBattleOver()) {
                        attackBtn.setDisable(true);
                        inventoryBtn.setDisable(true);
                        fleeBtn.setDisable(true);
                    }

                    populateInventory(attackBtn, inventoryBtn, fleeBtn);
                });

                row.getChildren().addAll(nameLabel, spacer, useBtn);
                inventoryItemsList.getChildren().add(row);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {

        Warrior warrior = new Warrior("Warrior");
        Zombie zombie = new Zombie();
        battle = new BattleManager(warrior, zombie);

        terminalArea = new TextArea();
        terminalArea.setEditable(false);
        terminalArea.setStyle(
                "-fx-control-inner-background: white; " +
                "-fx-text-fill: #000000ff; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 14px;");
        terminalArea.setText("=== COMBATTIMENTO ===\n" + warrior.getName() + " vs " + zombie.getName() + "\n");

        playerStatusLabel = new Label();
        playerStatusLabel.setStyle(
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #ffffff;");

        enemyStatusLabel = new Label();
        enemyStatusLabel.setStyle(
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #ffffff;");

        updateStatus();

        Button attackBtn = new Button("Attacca");
        Button fleeBtn = new Button("Fuggi");
        Button inventoryBtn = new Button("Inventario");
        Button saveBtn = new Button("Salva Gioco");
        Button loadBtn = new Button("Carica Gioco");
        Button settingsBtn = new Button("Impostazioni");

        GameSaveManager saveManager = new GameSaveManager();

        attackBtn.setOnAction(e -> {
            battle.playerAttack();
            if (!battle.isBattleOver()) {
                battle.enemyAttack();
            }
            terminalArea.setText(battle.getLog());
            updateStatus();
            if (battle.isBattleOver()) {
                attackBtn.setDisable(true);
                inventoryBtn.setDisable(true);
                fleeBtn.setDisable(true);
            }
        });

        fleeBtn.setOnAction(e -> {
            boolean fled = battle.tryFlee();
            terminalArea.setText(battle.getLog());
            updateStatus();
            if (fled || battle.isBattleOver()) {
                attackBtn.setDisable(true);
                inventoryBtn.setDisable(true);
                fleeBtn.setDisable(true);
            }
        });

        saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva la partita");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON (*.json)", "*.json"));

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
                    updateStatus();

                    attackBtn.setDisable(false);
                    inventoryBtn.setDisable(false);
                    fleeBtn.setDisable(false);

                    if (inventoryStage != null && inventoryStage.isShowing()) {
                        populateInventory(attackBtn, inventoryBtn, fleeBtn);
                    }
                } catch (Exception ex) {
                    terminalArea.appendText("\nErrore durante il caricamento: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        inventoryBtn.setOnAction(e -> {
            if (inventoryStage != null && inventoryStage.isShowing()) {
                inventoryStage.toFront();
                populateInventory(attackBtn, inventoryBtn, fleeBtn);
                return;
            }

            inventoryStage = new Stage();
            inventoryStage.setTitle("Inventario - Oggetti Usabili");

            VBox layout = new VBox(15);
            layout.setPadding(new Insets(20));
            layout.setStyle("-fx-background-color: #121214;");

            Label titleLabel = new Label("OGGETTI USABILI");
            titleLabel.setStyle(
                    "-fx-text-fill: #ffffff; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 16px;"
            );
            layout.getChildren().add(titleLabel);

            inventoryItemsList = new VBox(10);
            populateInventory(attackBtn, inventoryBtn, fleeBtn);

            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(inventoryItemsList);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            layout.getChildren().add(scrollPane);

            Button closeBtn = new Button("Chiudi");
            closeBtn.setStyle(
                    "-fx-background-color: #3e3e4a; " +
                    "-fx-text-fill: #ffffff; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 6 12; " +
                    "-fx-background-radius: 4; " +
                    "-fx-cursor: hand;"
            );
            closeBtn.setMaxWidth(Double.MAX_VALUE);
            closeBtn.setOnAction(evt -> inventoryStage.close());
            layout.getChildren().add(closeBtn);

            Scene scene = new Scene(layout, 350, 400);
            inventoryStage.setScene(scene);
            inventoryStage.show();
        });

        settingsBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(battle.getPlayer().getName());
            dialog.setTitle("Impostazioni");
            dialog.setHeaderText("Modifica Nome del Giocatore");
            dialog.setContentText("Inserisci il nuovo nome:");
            dialog.showAndWait().ifPresent(newName -> {
                if (!newName.trim().isEmpty()) {
                    battle.getPlayer().setName(newName.trim());
                    updateStatus();
                    terminalArea.appendText("\n[Impostazioni] Nome del giocatore cambiato in: " + newName.trim() + "\n");
                }
            });
        });

        String sidebarBtnStyle = "-fx-background-color: #3e3e4a; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 13px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;";

        saveBtn.setStyle(sidebarBtnStyle);
        loadBtn.setStyle(sidebarBtnStyle);
        settingsBtn.setStyle(sidebarBtnStyle);

        String combatBtnStyle = "-fx-background-color: #00FF00; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;";

        attackBtn.setStyle(combatBtnStyle);
        inventoryBtn.setStyle(combatBtnStyle.replace("#00FF00", "#00FFFF"));
        fleeBtn.setStyle(combatBtnStyle.replace("#00FF00", "#FF4500").replace("#000000", "#FFFFFF"));

        saveBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        settingsBtn.setMaxWidth(Double.MAX_VALUE);

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle(
                "-fx-background-color: #1e1e24; " +
                "-fx-border-color: #2e2e38; " +
                "-fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(200);

        Label menuTitle = new Label("MENU RPG");
        menuTitle.setStyle(
                "-fx-text-fill: #ffffff; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px; " +
                "-fx-padding: 0 0 10 0;");

        sidebar.getChildren().addAll(menuTitle, saveBtn, loadBtn, settingsBtn);

        VBox mainArea = new VBox(10);
        mainArea.setPadding(new Insets(15));
        mainArea.setStyle("-fx-background-color: #121214;");

        HBox statusContainer = new HBox();
        statusContainer.setStyle(
                "-fx-background-color: #1c1c1f; " +
                "-fx-padding: 10; " +
                "-fx-background-radius: 5;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        statusContainer.getChildren().addAll(playerStatusLabel, spacer, enemyStatusLabel);

        HBox combatButtonBar = new HBox(15, attackBtn, inventoryBtn, fleeBtn);
        combatButtonBar.setPadding(new Insets(10, 0, 0, 0));

        VBox.setVgrow(terminalArea, Priority.ALWAYS);
        mainArea.getChildren().addAll(terminalArea, statusContainer, combatButtonBar);
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        HBox root = new HBox(sidebar, mainArea);

        primaryStage.setTitle("RPG - Matricola 125556");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
