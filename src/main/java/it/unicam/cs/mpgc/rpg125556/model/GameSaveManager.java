package it.unicam.cs.mpgc.rpg125556.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class GameSaveManager {
    private final ObjectMapper mapper;

    public GameSaveManager() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveGame(File file, GameState state) throws IOException {
        mapper.writeValue(file, state);
    }

    public GameState loadGame(File file) throws IOException {
        return mapper.readValue(file, GameState.class);
    }
}
