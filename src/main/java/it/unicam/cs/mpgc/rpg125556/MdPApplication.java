package it.unicam.cs.mpgc.rpg125556;

import it.unicam.cs.mpgc.rpg125556.model.Armor;
import it.unicam.cs.mpgc.rpg125556.model.BattleManager;
import it.unicam.cs.mpgc.rpg125556.model.Enemy;
import it.unicam.cs.mpgc.rpg125556.model.GameSaveManager;
import it.unicam.cs.mpgc.rpg125556.model.GameState;
import it.unicam.cs.mpgc.rpg125556.model.Hero;
import it.unicam.cs.mpgc.rpg125556.model.Mage;
import it.unicam.cs.mpgc.rpg125556.model.Warrior;
import it.unicam.cs.mpgc.rpg125556.model.Weapon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.stream.Collectors;

public class MdPApplication extends Application {

    private TextArea terminalArea;
    private Label playerStatusLabel;
    private Label enemyStatusLabel;
    private BattleManager battle;
    private Stage inventoryStage;
    private VBox inventoryItemsList;
    private Stage infoStage;
    private VBox infoLayoutBox;
    private Stage primaryStage;
    private Scene landingScene;

    private void updateStatus() {
        playerStatusLabel.setText(battle.getPlayer().getName() + " (LV " + battle.getPlayer().getLevel() + ") HP: "
                + battle.getPlayer().getHealth() + "/" + battle.getPlayer().getMaxHealth() + " | EXP: "
                + battle.getPlayer().getExperience() + "/" + battle.getPlayer().getMaxExperience());
        enemyStatusLabel.setText(battle.getEnemy().getName() + " HP: " + battle.getEnemy().getHealth() + "/"
                + battle.getEnemy().getMaxHealth());
    }

    // Configurazione salvataggi
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

    // Personalizzazione grafica ComboBox
    private void styleComboBox(ComboBox<String> comboBox) {
        String comboStyle = "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-border-color: #3e3e4a; -fx-border-width: 1; -fx-border-radius: 3; -fx-background-radius: 3; -fx-font-family: 'Courier New'; -fx-font-size: 13px;";
        comboBox.setStyle(comboStyle);
        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #2a2a32;");
                } else {
                    setText(item);
                    setTextFill(Color.web("#cccccc"));
                    setStyle(
                            "-fx-background-color: #2a2a32; -fx-text-fill: #cccccc; -fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-padding: 5 10;");
                }
            }
        });
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(Color.web("#cccccc"));
                    setStyle(
                            "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-family: 'Courier New'; -fx-font-size: 13px;");
                }
            }
        });
    }

    // Traduzione termini da inglese a italiano
    private String translateText(String text) {
        if (text == null)
            return null;
        return text.replace("Health Potion", "Pozione di Salute")
                .replace("Flesh", "Carne")
                .replace("Iron Sword", "Spada di Ferro")
                .replace("Wooden Shield", "Scudo di Legno")
                .replace("Ancient Sword", "Spada Antica")
                .replace("Broken Shield", "Scudo Rotto")
                .replace("Warrior", "Guerriero")
                .replace("Mage", "Mago")
                .replace("Skeleton", "Scheletro")
                .replace("Zombie", "Zombie");
    }

    private void checkBattleStatus(Button attackBtn, Button inventoryBtn, Button fleeBtn, Button newEncounterBtn) {
        if (battle.getPlayer().isDead()) {
            attackBtn.setDisable(true);
            inventoryBtn.setDisable(true);
            fleeBtn.setDisable(true);
            newEncounterBtn.setVisible(false);
            newEncounterBtn.setManaged(false);

            if (inventoryStage != null) {
                inventoryStage.close();
            }
            if (infoStage != null) {
                infoStage.close();
            }

            // Schermata game over
            Stage gameOverStage = new Stage();
            gameOverStage.initModality(Modality.APPLICATION_MODAL);
            gameOverStage.initOwner(primaryStage);
            gameOverStage.setTitle("GAME OVER");

            VBox layout = new VBox(20);
            layout.setPadding(new Insets(25));
            layout.setStyle(
                    "-fx-background-color: #121214; -fx-alignment: center; -fx-border-color: #FF4500; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");

            Label titleLabel = new Label("GAME OVER");
            titleLabel.setStyle(
                    "-fx-text-fill: #FF4500; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 24px; -fx-effect: dropshadow(three-pass-box, rgba(255, 69, 0, 0.4), 10, 0, 0, 0);");

            Label msgLabel = new Label("Il tuo eroe è stato sconfitto in battaglia!");
            msgLabel.setStyle(
                    "-fx-text-fill: #ffffff; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-alignment: center;");

            Button menuBtn = new Button("TORNA AL MENU");
            String btnStyle = "-fx-background-color: #FF4500; -fx-text-fill: #ffffff; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;";
            menuBtn.setStyle(btnStyle);
            menuBtn.setOnMouseEntered(ev -> menuBtn
                    .setStyle(btnStyle + "-fx-effect: dropshadow(three-pass-box, rgba(255, 69, 0, 0.5), 8, 0, 0, 0);"));
            menuBtn.setOnMouseExited(ev -> menuBtn.setStyle(btnStyle));

            menuBtn.setOnAction(ev -> {
                primaryStage.setScene(landingScene);
                gameOverStage.close();
            });

            layout.getChildren().addAll(titleLabel, msgLabel, menuBtn);
            Scene scene = new Scene(layout, 350, 180);
            gameOverStage.setScene(scene);
            gameOverStage.showAndWait();
        } else if (battle.getEnemy().isDead()) {
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

        java.util.List<it.unicam.cs.mpgc.rpg125556.model.Potion> potions = battle.getPlayer().getInventory()
                .getItemsByClass(it.unicam.cs.mpgc.rpg125556.model.Potion.class);

        if (potions.isEmpty()) {
            Label emptyLabel = new Label("Nessun oggetto usabile disponibile.");
            emptyLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-family: 'Courier New'; -fx-font-size: 13px;");
            inventoryItemsList.getChildren().add(emptyLabel);
        } else {
            // Raggruppamento pozioni per nome
            java.util.Map<String, java.util.List<it.unicam.cs.mpgc.rpg125556.model.Potion>> groupedPotions = potions
                    .stream().collect(Collectors.groupingBy(
                            it.unicam.cs.mpgc.rpg125556.model.Potion::getName,
                            java.util.LinkedHashMap::new,
                            Collectors.toList()));

            for (java.util.Map.Entry<String, java.util.List<it.unicam.cs.mpgc.rpg125556.model.Potion>> entry : groupedPotions
                    .entrySet()) {
                java.util.List<it.unicam.cs.mpgc.rpg125556.model.Potion> list = entry.getValue();
                it.unicam.cs.mpgc.rpg125556.model.Potion firstPotion = list.get(0);
                int count = list.size();

                HBox row = new HBox(10);
                row.setStyle("-fx-background-color: #1c1c1f; -fx-padding: 8 12; -fx-background-radius: 5;");
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                String desc = translateText(firstPotion.getName());
                if (firstPotion instanceof it.unicam.cs.mpgc.rpg125556.model.HealthPotion hp) {
                    desc += " (Cura: " + hp.getHealAmount() + " HP)";
                }
                if (count > 1) {
                    desc += " x" + count;
                }

                Label nameLabel = new Label(desc);
                nameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-family: 'Courier New'; -fx-font-size: 13px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button useBtn = new Button("Usa");
                useBtn.setStyle(
                        "-fx-background-color: #00FF00; " +
                                "-fx-text-fill: #000000; " +
                                "-fx-font-family: 'Courier New'; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 4 10; " +
                                "-fx-background-radius: 3; " +
                                "-fx-cursor: hand;");

                useBtn.setOnAction(evt -> {
                    firstPotion.use(battle.getPlayer());
                    battle.getPlayer().getInventory().removeItem(firstPotion);

                    String logMessage = battle.getPlayer().getName() + " usa " + translateText(firstPotion.getName());
                    if (firstPotion instanceof it.unicam.cs.mpgc.rpg125556.model.HealthPotion hp) {
                        logMessage += " e recupera " + hp.getHealAmount() + " HP.";
                    } else {
                        logMessage += ".";
                    }
                    battle.getBattleLogList().add(logMessage);

                    if (!battle.isBattleOver()) {
                        battle.enemyAttack();
                    }

                    terminalArea.setText(translateText(battle.getLog()));
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
        nameLabel.setStyle(
                "-fx-text-fill: #ffffff; -fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button renameBtn = new Button("Rinomina");
        renameBtn.setStyle(
                "-fx-background-color: #3e3e4a; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-font-size: 11px; " +
                        "-fx-padding: 3 8; " +
                        "-fx-background-radius: 3; " +
                        "-fx-cursor: hand;");

        renameBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(battle.getPlayer().getName());
            dialog.setTitle("Rinomina");
            dialog.setHeaderText("Modifica Nome del Giocatore");
            dialog.setContentText("Inserisci il nuovo nome:");
            dialog.showAndWait().ifPresent(newName -> {
                if (!newName.trim().isEmpty()) {
                    battle.getPlayer().setName(newName.trim());
                    updateStatus();
                    terminalArea
                            .appendText("\n[Impostazioni] Nome del giocatore cambiato in: " + newName.trim() + "\n");
                    populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
                }
            });
        });

        nameRow.getChildren().addAll(nameLabel, spacer, renameBtn);
        infoLayoutBox.getChildren().add(nameRow);

        VBox statsBox = new VBox(8);
        statsBox.setStyle("-fx-background-color: #1c1c1f; -fx-padding: 12; -fx-background-radius: 5;");

        String statStyle = "-fx-text-fill: #cccccc; -fx-font-family: 'Courier New'; -fx-font-size: 13px;";
        String statBtnStyle = "-fx-background-color: #00FF00; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 11px; " +
                "-fx-padding: 2 6; " +
                "-fx-background-radius: 3; " +
                "-fx-cursor: hand;";

        Label lvLabel = new Label("Livello:   " + battle.getPlayer().getLevel());
        lvLabel.setStyle(statStyle);

        Label expLabel = new Label(
                "Esperienza: " + battle.getPlayer().getExperience() + " / " + battle.getPlayer().getMaxExperience());
        expLabel.setStyle(statStyle);

        HBox hpRow = new HBox(10);
        hpRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label hpLabel = new Label(
                "Salute:    " + battle.getPlayer().getHealth() + " / " + battle.getPlayer().getMaxHealth());
        hpLabel.setStyle(statStyle);
        hpRow.getChildren().add(hpLabel);
        if (battle.getPlayer().getStatPoints() > 0) {
            Region hpSpacer = new Region();
            HBox.setHgrow(hpSpacer, Priority.ALWAYS);
            Button addHpBtn = new Button("+");
            addHpBtn.setStyle(statBtnStyle);
            addHpBtn.setOnAction(e -> {
                battle.getPlayer().allocatePointToMaxHealth();
                updateStatus();
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            });
            hpRow.getChildren().addAll(hpSpacer, addHpBtn);
        }

        HBox atkRow = new HBox(10);
        atkRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label atkLabel = new Label("Attacco:   " + battle.getPlayer().getAttack());
        atkLabel.setStyle(statStyle);
        atkRow.getChildren().add(atkLabel);
        if (battle.getPlayer().getStatPoints() > 0) {
            Region atkSpacer = new Region();
            HBox.setHgrow(atkSpacer, Priority.ALWAYS);
            Button addAtkBtn = new Button("+");
            addAtkBtn.setStyle(statBtnStyle);
            addAtkBtn.setOnAction(e -> {
                battle.getPlayer().allocatePointToAttack();
                updateStatus();
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            });
            atkRow.getChildren().addAll(atkSpacer, addAtkBtn);
        }

        HBox defRow = new HBox(10);
        defRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label defLabel = new Label("Difesa:    " + battle.getPlayer().getDefense());
        defLabel.setStyle(statStyle);
        defRow.getChildren().add(defLabel);
        if (battle.getPlayer().getStatPoints() > 0) {
            Region defSpacer = new Region();
            HBox.setHgrow(defSpacer, Priority.ALWAYS);
            Button addDefBtn = new Button("+");
            addDefBtn.setStyle(statBtnStyle);
            addDefBtn.setOnAction(e -> {
                battle.getPlayer().allocatePointToDefense();
                updateStatus();
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            });
            defRow.getChildren().addAll(defSpacer, addDefBtn);
        }

        HBox spdRow = new HBox(10);
        spdRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label spdLabel = new Label("Velocità:  " + battle.getPlayer().getSpeed());
        spdLabel.setStyle(statStyle);
        spdRow.getChildren().add(spdLabel);
        if (battle.getPlayer().getStatPoints() > 0) {
            Region spdSpacer = new Region();
            HBox.setHgrow(spdSpacer, Priority.ALWAYS);
            Button addSpdBtn = new Button("+");
            addSpdBtn.setStyle(statBtnStyle);
            addSpdBtn.setOnAction(e -> {
                battle.getPlayer().allocatePointToSpeed();
                updateStatus();
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            });
            spdRow.getChildren().addAll(spdSpacer, addSpdBtn);
        }

        statsBox.getChildren().addAll(lvLabel, expLabel, hpRow, atkRow, defRow, spdRow);

        if (battle.getPlayer().getStatPoints() > 0) {
            Label pointsLabel = new Label("Punti statistica disponibili: " + battle.getPlayer().getStatPoints());
            pointsLabel.setStyle(
                    "-fx-text-fill: #00FF00; -fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 0;");
            statsBox.getChildren().add(pointsLabel);
        }

        infoLayoutBox.getChildren().add(statsBox);

        Hero player = battle.getPlayer();

        VBox equipBox = new VBox(8);
        equipBox.setStyle("-fx-background-color: #1c1c1f; -fx-padding: 12; -fx-background-radius: 5;");

        Label equipTitle = new Label("EQUIPAGGIAMENTO");
        equipTitle.setStyle(
                "-fx-text-fill: #ffffff; -fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 0 5 0;");

        Label weaponLabel = new Label("Arma:");
        weaponLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        ComboBox<String> weaponCombo = new ComboBox<>();
        weaponCombo.getItems().add("[Nessuna Arma]");
        java.util.List<Weapon> weapons = player.getInventory().getItemsByClass(Weapon.class);
        for (Weapon w : weapons) {
            weaponCombo.getItems().add(translateText(w.getName()) + " (+" + w.getAttackBonus() + " ATK)");
        }

        if (player.getEquippedWeapon() != null) {
            Weapon ew = player.getEquippedWeapon();
            weaponCombo.setValue(translateText(ew.getName()) + " (+" + ew.getAttackBonus() + " ATK)");
        } else {
            weaponCombo.setValue("[Nessuna Arma]");
        }

        weaponCombo.setOnAction(e -> {
            String selected = weaponCombo.getValue();
            if (selected == null)
                return;
            if (selected.equals("[Nessuna Arma]")) {
                player.unequipWeapon();
            } else {
                for (Weapon w : weapons) {
                    String label = translateText(w.getName()) + " (+" + w.getAttackBonus() + " ATK)";
                    if (label.equals(selected)) {
                        player.equipWeapon(w);
                        break;
                    }
                }
            }
            updateStatus();
            populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
        });

        Label armorLabel = new Label("Armatura:");
        armorLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        ComboBox<String> armorCombo = new ComboBox<>();
        armorCombo.getItems().add("[Nessuna Armatura]");
        java.util.List<Armor> armors = player.getInventory().getItemsByClass(Armor.class);
        for (Armor a : armors) {
            armorCombo.getItems().add(translateText(a.getName()) + " (+" + a.getDefenseBonus() + " DEF)");
        }

        if (player.getEquippedArmor() != null) {
            Armor ea = player.getEquippedArmor();
            armorCombo.setValue(translateText(ea.getName()) + " (+" + ea.getDefenseBonus() + " DEF)");
        } else {
            armorCombo.setValue("[Nessuna Armatura]");
        }

        armorCombo.setOnAction(e -> {
            String selected = armorCombo.getValue();
            if (selected == null)
                return;
            if (selected.equals("[Nessuna Armatura]")) {
                player.unequipArmor();
            } else {
                for (Armor a : armors) {
                    String label = translateText(a.getName()) + " (+" + a.getDefenseBonus() + " DEF)";
                    if (label.equals(selected)) {
                        player.equipArmor(a);
                        break;
                    }
                }
            }
            updateStatus();
            populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
        });

        styleComboBox(weaponCombo);
        styleComboBox(armorCombo);
        weaponCombo.setMaxWidth(Double.MAX_VALUE);
        armorCombo.setMaxWidth(Double.MAX_VALUE);

        equipBox.getChildren().addAll(equipTitle, weaponLabel, weaponCombo, armorLabel, armorCombo);
        infoLayoutBox.getChildren().add(equipBox);
    }

    @Override
    public void start(Stage primaryStage) {
        // Schermata principale
        this.primaryStage = primaryStage;
        Warrior dummyWarrior = new Warrior("Guerriero");
        Enemy dummyZombie = Enemy.getRandomEnemy();
        battle = new BattleManager(dummyWarrior, dummyZombie);

        terminalArea = new TextArea();
        terminalArea.setEditable(false);
        terminalArea.setStyle(
                "-fx-control-inner-background: white; " +
                        "-fx-text-fill: #000000ff; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-font-size: 14px;");
        terminalArea
                .setText("=== COMBATTIMENTO ===\n" + dummyWarrior.getName() + " vs " + dummyZombie.getName() + "\n");

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

        Button saveBtn = new Button("Salva Partita");

        Button loadBtn = new Button("Carica Partita");

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
            terminalArea.setText(translateText(battle.getLog()));
            updateStatus();
            checkBattleStatus(attackBtn, inventoryBtn, fleeBtn, newEncounterBtn);
            if (infoStage != null && infoStage.isShowing()) {
                populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            }
        });

        fleeBtn.setOnAction(e -> {
            boolean fled = battle.tryFlee();
            terminalArea.setText(translateText(battle.getLog()));
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
                terminalArea.setText(translateText(battle.getLog()));
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
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("yyyy-MM-dd_HH-mm-ss");
            String defaultName = "save_" + playerName + "_" + now.format(formatter) + ".json";
            fileChooser.setInitialFileName(defaultName);

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    GameState state = new GameState(battle.getPlayer(), battle.getEnemy(), battle.getBattleLogList());
                    saveManager.saveGame(file, state);
                    terminalArea.appendText("\n--- PARTITA SALVATA CON SUCCESSO ---\n");
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

                    terminalArea.setText(translateText(battle.getLog()) + "\n--- PARTITA CARICATA ---\n");
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

            // Inventario
            inventoryStage = new Stage();
            inventoryStage.setTitle("Inventario - Oggetti Usabili");

            VBox layout = new VBox(15);
            layout.setPadding(new Insets(20));
            layout.setStyle("-fx-background-color: #121214;");

            Label titleLabel = new Label("OGGETTI USABILI");
            titleLabel.setStyle(
                    "-fx-text-fill: #ffffff; " +
                            "-fx-font-family: 'Courier New'; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 16px;");
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
                            "-fx-font-family: 'Courier New'; " +
                            "-fx-font-size: 13px; " +
                            "-fx-padding: 6 12; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;");
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

            // Scheda personaggio
            infoStage = new Stage();
            infoStage.setTitle("Scheda Personaggio");

            VBox layout = new VBox(15);
            layout.setPadding(new Insets(20));
            layout.setStyle("-fx-background-color: #121214;");

            Label titleLabel = new Label("SCHEDA PERSONAGGIO");
            titleLabel.setStyle(
                    "-fx-text-fill: #ffffff; " +
                            "-fx-font-family: 'Courier New'; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 16px;");
            layout.getChildren().add(titleLabel);

            infoLayoutBox = new VBox(15);
            populateCharacterInfo(attackBtn, inventoryBtn, fleeBtn);
            layout.getChildren().add(infoLayoutBox);

            Button closeBtn = new Button("Chiudi");
            closeBtn.setStyle(
                    "-fx-background-color: #3e3e4a; " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-font-family: 'Courier New'; " +
                            "-fx-font-size: 13px; " +
                            "-fx-padding: 6 12; " +
                            "-fx-background-radius: 4; " +
                            "-fx-cursor: hand;");
            closeBtn.setMaxWidth(Double.MAX_VALUE);
            closeBtn.setOnAction(evt -> infoStage.close());
            layout.getChildren().add(closeBtn);

            Scene scene = new Scene(layout, 350, 480);
            String css = ".combo-box { -fx-background-color: transparent; } " +
                    ".combo-box > .arrow-button { -fx-background-color: transparent; } " +
                    ".combo-box > .arrow-button > .arrow { -fx-background-color: #cccccc; } " +
                    ".combo-box > .list-cell { -fx-text-fill: #cccccc !important; } " +
                    ".combo-box > .list-cell:selected { -fx-text-fill: #cccccc !important; } " +
                    ".combo-box-popup > .list-view { -fx-background-color: #2a2a32; -fx-border-color: #3e3e4a; -fx-border-width: 1px; } "
                    +
                    ".combo-box-popup > .list-view > .virtual-flow > .clipped-container > .sheet > .list-cell { -fx-text-fill: #cccccc !important; -fx-background-color: #2a2a32; } "
                    +
                    ".combo-box-popup > .list-view > .virtual-flow > .clipped-container > .sheet > .list-cell:selected { -fx-text-fill: #cccccc !important; -fx-background-color: #3e3e4a; } "
                    +
                    ".combo-box-popup > .list-view > .virtual-flow > .clipped-container > .sheet > .list-cell:hover { -fx-text-fill: #cccccc !important; -fx-background-color: #3e3e4a; }";
            scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
            infoStage.setScene(scene);
            infoStage.show();
        });

        newEncounterBtn.setOnAction(e -> {
            Enemy newEnemy = Enemy.getRandomEnemy();
            battle = new BattleManager(battle.getPlayer(), newEnemy);

            battle.getBattleLogList().add("=== NUOVO COMBATTIMENTO ===\n" + battle.getPlayer().getName() + " vs "
                    + battle.getEnemy().getName() + "\n");

            terminalArea.setText(translateText(battle.getLog()));
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
                        "-fx-cursor: hand;");
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
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 0, 0.4), 10, 0, 0, 0);");

        Label gameSubtitle = new Label("Matricola 125556");
        gameSubtitle.setStyle(
                "-fx-text-fill: #8e8e9f; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 0 0 30 0;");

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

        newGameBtn.setOnMouseEntered(
                ev -> newGameBtn.setStyle(landingBtnStyle.replace("#1e1e24", "#00FF00").replace("#ffffff", "#000000")
                        + "-fx-effect: dropshadow(three-pass-box, rgba(0,255,0,0.5), 10, 0, 0, 0);"));
        newGameBtn.setOnMouseExited(
                ev -> newGameBtn.setStyle(landingBtnStyle.replace("#1e1e24", "#00FF00").replace("#ffffff", "#000000")));
        loadGameBtn.setOnMouseEntered(ev -> loadGameBtn
                .setStyle(landingBtnStyle + "-fx-background-color: #2a2a32; -fx-border-color: #00FF00;"));
        loadGameBtn.setOnMouseExited(ev -> loadGameBtn.setStyle(landingBtnStyle));

        newGameBtn.setOnAction(ev -> {
            // Selezione classe
            Stage classStage = new Stage();
            classStage.initModality(Modality.APPLICATION_MODAL);
            classStage.initOwner(primaryStage);
            classStage.setTitle("Selezione Classe");

            VBox layout = new VBox(20);
            layout.setPadding(new Insets(20));
            layout.setStyle(
                    "-fx-background-color: #121214; -fx-alignment: center; -fx-border-color: #00FF00; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

            Label titleLabel = new Label("SCEGLI LA CLASSE");
            titleLabel.setStyle(
                    "-fx-text-fill: #ffffff; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px;");

            String cardStyle = "-fx-background-color: #1c1c1f; -fx-padding: 15; -fx-background-radius: 8; -fx-alignment: center; -fx-cursor: hand; -fx-min-width: 260px; -fx-border-color: #2e2e38; -fx-border-width: 1;";

            VBox warriorCard = new VBox(5);
            warriorCard.setStyle(cardStyle);
            Label wName = new Label("GUERRIERO");
            wName.setStyle(
                    "-fx-text-fill: #00FFFF; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;");
            Label wStats = new Label("HP: 100 | ATK: 50 | DEF: 30 | SPD: 20");
            wStats.setStyle("-fx-text-fill: #aaaaaa; -fx-font-family: 'Courier New'; -fx-font-size: 11px;");
            warriorCard.getChildren().addAll(wName, wStats);

            VBox mageCard = new VBox(5);
            mageCard.setStyle(cardStyle);
            Label mName = new Label("MAGO");
            mName.setStyle(
                    "-fx-text-fill: #FF00FF; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;");
            Label mStats = new Label("HP: 80 | ATK: 80 | DEF: 15 | SPD: 25");
            mStats.setStyle("-fx-text-fill: #aaaaaa; -fx-font-family: 'Courier New'; -fx-font-size: 11px;");
            mageCard.getChildren().addAll(mName, mStats);

            warriorCard.setOnMouseEntered(
                    e -> warriorCard.setStyle(cardStyle + "-fx-border-color: #00FFFF; -fx-background-color: #25252a;"));
            warriorCard.setOnMouseExited(e -> warriorCard.setStyle(cardStyle));
            mageCard.setOnMouseEntered(
                    e -> mageCard.setStyle(cardStyle + "-fx-border-color: #FF00FF; -fx-background-color: #25252a;"));
            mageCard.setOnMouseExited(e -> mageCard.setStyle(cardStyle));

            warriorCard.setOnMouseClicked(e -> {
                Hero player = new Warrior("Guerriero");
                Enemy enemy = Enemy.getRandomEnemy();
                battle = new BattleManager(player, enemy);
                terminalArea.setText("=== COMBATTIMENTO ===\n" + player.getName() + " vs " + enemy.getName() + "\n");
                updateStatus();
                attackBtn.setDisable(false);
                inventoryBtn.setDisable(false);
                fleeBtn.setDisable(false);
                newEncounterBtn.setVisible(false);
                newEncounterBtn.setManaged(false);
                primaryStage.setScene(battleScene);
                classStage.close();
            });

            mageCard.setOnMouseClicked(e -> {
                Hero player = new Mage("Mago");
                Enemy enemy = Enemy.getRandomEnemy();
                battle = new BattleManager(player, enemy);
                terminalArea.setText("=== COMBATTIMENTO ===\n" + player.getName() + " vs " + enemy.getName() + "\n");
                updateStatus();
                attackBtn.setDisable(false);
                inventoryBtn.setDisable(false);
                fleeBtn.setDisable(false);
                newEncounterBtn.setVisible(false);
                newEncounterBtn.setManaged(false);
                primaryStage.setScene(battleScene);
                classStage.close();
            });

            layout.getChildren().addAll(titleLabel, warriorCard, mageCard);
            Scene scene = new Scene(layout, 320, 320);
            classStage.setScene(scene);
            classStage.showAndWait();
        });

        loadGameBtn.setOnAction(ev -> {
            FileChooser fileChooser = createFileChooser("Carica una partita");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    GameState state = saveManager.loadGame(file);
                    battle = new BattleManager(state.getPlayer(), state.getEnemy());
                    battle.setBattleLog(state.getBattleLog());

                    terminalArea.setText(translateText(battle.getLog()) + "\n--- PARTITA CARICATA ---\n");
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
        this.landingScene = new Scene(landingLayout, 900, 600);

        primaryStage.setTitle("RPG - Matricola 125556");
        primaryStage.setScene(this.landingScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
