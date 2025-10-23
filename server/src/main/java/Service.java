import chess.ChessGame;
import dataaccess.AuthData;
import dataaccess.DataAccess;
import dataaccess.GameData;
import dataaccess.MemoryDataAccess;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;

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

    public GameData getGame(int gameID) {
        return dataAccess.getGame(gameID);
    }

    public int createGame(String gameName) {
        int newID = 1 + dataAccess.getGames().stream().mapToInt(GameData::gameID).max().orElse(0);
        GameData game = new GameData(newID, null, null, gameName, new ChessGame());
        dataAccess.saveGame(game);
        return newID;
    }

    public void joinGame(int gameID, String username, ChessGame.TeamColor color) {
        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new BadRequestResponse("invalid game id");
        }
        if (color == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) {
                throw new ForbiddenResponse("already taken");
            }
            GameData newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
            dataAccess.saveGame(newGame);
        } else if (color == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new ForbiddenResponse("already taken");
            }
            GameData newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
            dataAccess.saveGame(newGame);
        }
    }

    public void clear() {
        dataAccess.clear();
    }
}
