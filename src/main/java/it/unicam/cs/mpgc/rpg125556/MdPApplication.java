package it.unicam.cs.mpgc.rpg125556;

import it.unicam.cs.mpgc.rpg125556.model.BattleManager;
import it.unicam.cs.mpgc.rpg125556.model.Warrior;
import it.unicam.cs.mpgc.rpg125556.model.Hero;
import it.unicam.cs.mpgc.rpg125556.model.Mage;
import it.unicam.cs.mpgc.rpg125556.model.Zombie;
import it.unicam.cs.mpgc.rpg125556.model.GameSaveManager;
import it.unicam.cs.mpgc.rpg125556.model.GameState;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
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
import java.util.Optional;

public class MdPApplication extends Application {

    private TextArea terminalArea;
    private Label playerStatusLabel;
    private Label enemyStatusLabel;
    private BattleManager battle;
    private Stage inventoryStage;
    private VBox inventoryItemsList;
    private Stage infoStage;
    private VBox infoLayoutBox;

    private void updateStatus() {
        playerStatusLabel.setText(battle.getPlayer().getName() + " (LV " + battle.getPlayer().getLevel() + ") HP: " + battle.getPlayer().getHealth() + "/" + battle.getPlayer().getMaxHealth() + " | EXP: " + battle.getPlayer().getExperience() + "/" + battle.getPlayer().getMaxExperience());
        enemyStatusLabel.setText(battle.getEnemy().getName() + " HP: " + battle.getEnemy().getHealth() + "/" + battle.getEnemy().getMaxHealth());
    }

    private FileChooser createFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON (*.json)", "*.json"));
        File saveDir = new File("saves");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        fileChooser.setInitialDirectory(saveDir);
        return fileChooser;
    }

    private void checkBattleStatus(Button attackBtn, Button inventoryBtn, Button fleeBtn, Button newEncounterBtn) {
        if (battle.isBattleOver()) {
            attackBtn.setDisable(true);
            inventoryBtn.setDisable(true);
            fleeBtn.setDisable(true);
            newEncounterBtn.setVisible(true);
            newEncounterBtn.setManaged(true);
        }
    }

    private void populateInventory(Button attackBtn, Button inventoryBtn, Button fleeBtn, Button newEncounterBtn) {
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

                    checkBattleStatus(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);

                    populateInventory(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
                    if (infoStage != null && infoStage.isShowing()) {
                        populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
                    }
                });

                row.getChildren().addAll(nameLabel, spacer, useBtn);
                inventoryItemsList.getChildren().add(row);
            }
        }
    }

    private void populateCharacterInfo(Button attackBtn, Button inventoryBtn, Button fleeBtn) {
        if (infoLayoutBox == null) {
            return;
        }
        infoLayoutBox.getChildren().clear();

        HBox nameRow = new HBox(10);
        nameRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label nameLabel = new Label("Nome: " + battle.getPlayer().getName());
        nameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button renameBtn = new Button("Rinomina");
        renameBtn.setStyle(
                "-fx-background-color: #3e3e4a; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 11px; " +
                "-fx-padding: 3 8; " +
                "-fx-background-radius: 3; " +
                "-fx-cursor: hand;"
        );

        renameBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(battle.getPlayer().getName());
            dialog.setTitle("Rinomina");
            dialog.setHeaderText("Modifica Nome del Giocatore");
            dialog.setContentText("Inserisci il nuovo nome:");
            dialog.showAndWait().ifPresent(newName -> {
                if (!newName.trim().isEmpty()) {
                    battle.getPlayer().setName(newName.trim());
                    updateStatus();
                    terminalArea.appendText("\n[Impostazioni] Nome del giocatore cambiato in: " + newName.trim() + "\n");
                    populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
                }
            });
        });

        nameRow.getChildren().addAll(nameLabel, spacer, renameBtn);
        infoLayoutBox.getChildren().add(nameRow);

        VBox statsBox = new VBox(8);
        statsBox.setStyle("-fx-background-color: #1c1c1f; -fx-padding: 12; -fx-background-radius: 5;");

        String statStyle = "-fx-text-fill: #cccccc; -fx-font-family: 'Courier New'; -fx-font-size: 13px;";

        Label lvLabel = new Label("Livello:   " + battle.getPlayer().getLevel());
        lvLabel.setStyle(statStyle);

        Label hpLabel = new Label("Salute:    " + battle.getPlayer().getHealth() + " / " + battle.getPlayer().getMaxHealth());
        hpLabel.setStyle(statStyle);

        Label expLabel = new Label("Esperienza: " + battle.getPlayer().getExperience() + " / " + battle.getPlayer().getMaxExperience());
        expLabel.setStyle(statStyle);

        Label atkLabel = new Label("Attacco:   " + battle.getPlayer().getAttack());
        atkLabel.setStyle(statStyle);

        Label defLabel = new Label("Difesa:    " + battle.getPlayer().getDefense());
        defLabel.setStyle(statStyle);

        Label spdLabel = new Label("Velocità:  " + battle.getPlayer().getSpeed());
        spdLabel.setStyle(statStyle);

        statsBox.getChildren().addAll(lvLabel, hpLabel, expLabel, atkLabel, defLabel, spdLabel);
        infoLayoutBox.getChildren().add(statsBox);
    }

    @Override
    public void start(Stage primaryStage) {

        Warrior dummyWarrior = new Warrior("Guerriero");
        Zombie dummyZombie = new Zombie();
        battle = new BattleManager(dummyWarrior, dummyZombie);

        terminalArea = new TextArea();
        terminalArea.setEditable(false);
        terminalArea.setStyle(
                "-fx-control-inner-background: white; " +
                "-fx-text-fill: #000000ff; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 14px;");
        terminalArea.setText("=== COMBATTIMENTO ===\n" + dummyWarrior.getName() + " vs " + dummyZombie.getName() + "\n");

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
        Button infoBtn = new Button("Info Personaggio");
        Button newEncounterBtn = new Button("Nuovo Incontro");

        newEncounterBtn.setVisible(false);
        newEncounterBtn.setManaged(false);

        GameSaveManager saveManager = new GameSaveManager();

        attackBtn.setOnAction(e -> {
            battle.playerAttack();
            if (!battle.isBattleOver()) {
                battle.enemyAttack();
            }
            terminalArea.setText(battle.getLog());
            updateStatus();
            checkBattleStatus(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
            if (infoStage != null && infoStage.isShowing()) {
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            }
        });

        fleeBtn.setOnAction(e -> {
            boolean fled = battle.tryFlee();
            terminalArea.setText(battle.getLog());
            updateStatus();
            if (fled) {
                attackBtn.setDisable(true);
                inventoryBtn.setDisable(true);
                fleeBtn.setDisable(true);
                newEncounterBtn.setVisible(true);
                newEncounterBtn.setManaged(true);
            } else {
                if (!battle.isBattleOver()) {
                    battle.enemyAttack();
                }
                terminalArea.setText(battle.getLog());
                updateStatus();
                checkBattleStatus(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
            }
            if (infoStage != null && infoStage.isShowing()) {
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            }
        });

        saveBtn.setOnAction(e -> {
            FileChooser fileChooser = createFileChooser("Salva la partita");

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
            FileChooser fileChooser = createFileChooser("Carica una partita");
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
                    newEncounterBtn.setVisible(false);
                    newEncounterBtn.setManaged(false);

                    checkBattleStatus(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);

                    if (inventoryStage != null && inventoryStage.isShowing()) {
                        populateInventory(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
                    }
                    if (infoStage != null && infoStage.isShowing()) {
                        populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
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
                populateInventory(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
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
            populateInventory(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);

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

        infoBtn.setOnAction(e -> {
            if (infoStage != null && infoStage.isShowing()) {
                infoStage.toFront();
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
                return;
            }

            infoStage = new Stage();
            infoStage.setTitle("Scheda Personaggio");

            VBox layout = new VBox(15);
            layout.setPadding(new Insets(20));
            layout.setStyle("-fx-background-color: #121214;");

            Label titleLabel = new Label("SCHEDA PERSONAGGIO");
            titleLabel.setStyle(
                    "-fx-text-fill: #ffffff; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 16px;"
            );
            layout.getChildren().add(titleLabel);

            infoLayoutBox = new VBox(15);
            populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            layout.getChildren().add(infoLayoutBox);

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
            closeBtn.setOnAction(evt -> infoStage.close());
            layout.getChildren().add(closeBtn);

            Scene scene = new Scene(layout, 320, 350);
            infoStage.setScene(scene);
            infoStage.show();
        });

        newEncounterBtn.setOnAction(e -> {
            Zombie newZombie = new Zombie();
            battle = new BattleManager(battle.getPlayer(), newZombie);

            battle.getBattleLogList().add("=== NUOVO COMBATTIMENTO ===\n" + battle.getPlayer().getName() + " vs " + battle.getEnemy().getName() + "\n");

            terminalArea.setText(battle.getLog());
            updateStatus();

            attackBtn.setDisable(false);
            inventoryBtn.setDisable(false);
            fleeBtn.setDisable(false);
            newEncounterBtn.setVisible(false);
            newEncounterBtn.setManaged(false);

            if (inventoryStage != null && inventoryStage.isShowing()) {
                populateInventory(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
            }
            if (infoStage != null && infoStage.isShowing()) {
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            }
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
        infoBtn.setStyle(sidebarBtnStyle);
        newEncounterBtn.setStyle(sidebarBtnStyle);

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
        infoBtn.setMaxWidth(Double.MAX_VALUE);

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

        sidebar.getChildren().addAll(menuTitle, saveBtn, loadBtn, infoBtn);

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

        newEncounterBtn.setStyle(
                "-fx-background-color: #00FF00; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
        );
        newEncounterBtn.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(terminalArea, Priority.ALWAYS);
        mainArea.getChildren().addAll(terminalArea, newEncounterBtn, statusContainer, combatButtonBar);
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        HBox root = new HBox(sidebar, mainArea);
        Scene battleScene = new Scene(root, 900, 600);

        VBox landingLayout = new VBox(20);
        landingLayout.setPadding(new Insets(50));
        landingLayout.setStyle("-fx-background-color: #121214; -fx-alignment: center;");

        Label gameTitle = new Label("RPG ADVENTURE");
        gameTitle.setStyle(
                "-fx-text-fill: #00FF00; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 36px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 0, 0.4), 10, 0, 0, 0);"
        );

        Label gameSubtitle = new Label("Matricola 125556");
        gameSubtitle.setStyle(
                "-fx-text-fill: #8e8e9f; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 16px; " +
                "-fx-padding: 0 0 30 0;"
        );

        Button newGameBtn = new Button("NUOVA PARTITA");
        Button loadGameBtn = new Button("CARICA PARTITA");

        String landingBtnStyle = "-fx-background-color: #1e1e24; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 15 40; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #2e2e38; " +
                "-fx-border-width: 1; " +
                "-fx-cursor: hand; " +
                "-fx-min-width: 250px;";

        newGameBtn.setStyle(landingBtnStyle.replace("#1e1e24", "#00FF00").replace("#ffffff", "#000000"));
        loadGameBtn.setStyle(landingBtnStyle);

        newGameBtn.setOnMouseEntered(ev -> newGameBtn.setStyle(landingBtnStyle.replace("#1e1e24", "#00FF00").replace("#ffffff", "#000000") + "-fx-effect: dropshadow(three-pass-box, rgba(0,255,0,0.5), 10, 0, 0, 0);"));
        newGameBtn.setOnMouseExited(ev -> newGameBtn.setStyle(landingBtnStyle.replace("#1e1e24", "#00FF00").replace("#ffffff", "#000000")));
        loadGameBtn.setOnMouseEntered(ev -> loadGameBtn.setStyle(landingBtnStyle + "-fx-background-color: #2a2a32; -fx-border-color: #00FF00;"));
        loadGameBtn.setOnMouseExited(ev -> loadGameBtn.setStyle(landingBtnStyle));

        newGameBtn.setOnAction(ev -> {
            java.util.List<String> classes = java.util.List.of("Guerriero", "Mago");
            ChoiceDialog<String> classDialog = new ChoiceDialog<>("Guerriero", classes);
            classDialog.setTitle("Selezione Classe");
            classDialog.setHeaderText("Scegli la classe del tuo eroe");
            classDialog.setContentText("Classe:");

            Optional<String> result = classDialog.showAndWait();
            if (result.isPresent()) {
                String chosenClass = result.get();
                Hero player;
                if (chosenClass.equals("Mago")) {
                    player = new Mage("Mago");
                } else {
                    player = new Warrior("Guerriero");
                }
                Zombie zombie = new Zombie();
                battle = new BattleManager(player, zombie);

                terminalArea.setText("=== COMBATTIMENTO ===\n" + player.getName() + " vs " + zombie.getName() + "\n");
                updateStatus();

                attackBtn.setDisable(false);
                inventoryBtn.setDisable(false);
                fleeBtn.setDisable(false);
                newEncounterBtn.setVisible(false);
                newEncounterBtn.setManaged(false);

                primaryStage.setScene(battleScene);
            }
        });

        loadGameBtn.setOnAction(ev -> {
            FileChooser fileChooser = createFileChooser("Carica una partita");
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
                    newEncounterBtn.setVisible(false);
                    newEncounterBtn.setManaged(false);

                    checkBattleStatus(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);

                    if (inventoryStage != null && inventoryStage.isShowing()) {
                        populateInventory(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
                    }
                    if (infoStage != null && infoStage.isShowing()) {
                        populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
                    }

                    primaryStage.setScene(battleScene);
                } catch (Exception ex) {
                    terminalArea.appendText("\nErrore durante il caricamento: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        landingLayout.getChildren().addAll(gameTitle, gameSubtitle, newGameBtn, loadGameBtn);
        Scene landingScene = new Scene(landingLayout, 900, 600);

        primaryStage.setTitle("RPG - Matricola 125556");
        primaryStage.setScene(landingScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
