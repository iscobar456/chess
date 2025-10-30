package service;

import chess.ChessGame;
import dataaccess.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Service {
    public DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(String username, String password, String email) {
        if (username == null) {
            throw new BadRequestResponse("must provide username");
        } else if (password == null) {
            throw new BadRequestResponse("must provide password");
        } else if (email == null) {
            throw new BadRequestResponse("must provide email");
        }

        UserData user = dataAccess.getUser(username);
        if (user != null) {
            throw new ForbiddenResponse("username already taken");
        }

        user = new UserData(username, BCrypt.hashpw(password, BCrypt.gensalt()), email);
        dataAccess.saveUser(user);
        AuthData auth = createAuth(username);
        dataAccess.saveAuth(auth);
        return auth;
    }

    public AuthData createAuth(String username) {
        if (username == null) {
            throw new BadRequestResponse("must provide username");
        }
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }

    public AuthData login(String username, String password) {
        if (username == null) {
            throw new BadRequestResponse("must provide username");
        } else if (password == null) {
            throw new BadRequestResponse("must provide password");
        }
        UserData user = dataAccess.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new UnauthorizedResponse("unauthorized");
        }
        AuthData auth = createAuth(username);
        dataAccess.saveAuth(auth);
        return auth;
    }

    public void deleteAuth(String authToken) {
        dataAccess.deleteAuth(authToken);
    }

    public String validateToken(String authToken) {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        return auth.username();
    }

    public ArrayList<GameData> getGames() {
        return dataAccess.getGames();
    }

    public GameData getGame(int gameID) {
        return dataAccess.getGame(gameID);
    }

    public int createGame(String gameName) {
        if (gameName == null) {
            throw new BadRequestResponse("must provide game name");
        }
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
        } else {
            throw new BadRequestResponse();
        }
    }

    public void clear() {
        dataAccess.clear();
    }
}
