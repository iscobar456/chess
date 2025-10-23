import chess.ChessGame;
import dataaccess.AuthData;
import dataaccess.DataAccess;
import dataaccess.GameData;
import dataaccess.MemoryDataAccess;

import java.util.ArrayList;
import java.util.Arrays;

public class Service {
    // I've made this public for now. Will change later.
    public MemoryDataAccess dataAccess = new MemoryDataAccess();

    public AuthData register(String username, String password, String email) {
        return null;
    }

    public AuthData login(String username, String password) {
        return null;
    }

    public void logout() {

    }

    public boolean isTokenValid(String authToken) {
        return true;
    }

    public ArrayList<GameData> getGames() {
        return dataAccess.getGames();
    }

    public int createGame(String gameName) {
        int newID = 1 + dataAccess.getGames().stream().mapToInt(GameData::gameID).max().orElse(0);
        GameData game = new GameData(newID, null, null, gameName, new ChessGame());
        dataAccess.saveGame(game);
        return newID;
    }

    public void joinGame(int gameID, ChessGame.TeamColor color) {

    }

    public void clear() {
        dataAccess.clear();
    }
}
