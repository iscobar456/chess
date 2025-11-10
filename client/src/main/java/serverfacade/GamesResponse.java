package serverfacade;

import chess.ChessGame;

import java.util.ArrayList;

public class GamesResponse {
    public record GameData(String gameName, int gameID, String whiteUsername, String blackUsername, ChessGame game) {}

    ArrayList<GameData> games;

    public GamesResponse(ArrayList<GameData> games) {
        this.games = games;
    }

    public ArrayList<GameData> getGames() {
        return games;
    }
}
