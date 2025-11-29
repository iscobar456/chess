package ui;

import serverfacade.GameData;

public record Update(String message, GameData gameData) {
}
