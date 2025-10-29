package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import chess.ChessGame;

public class DataAccessTests {
    private final SQLDataAccess dataAccess = new SQLDataAccess();

    @Test
    void getGame() throws SQLException, DataAccessException {
        // Create users
        String username1 = "user1";
        String username2 = "user2";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));
        dataAccess.saveUser(new UserData(username2, password, email));

        // Create game
        int gameID = 1;
        String gameName = "game1";
        ChessGame chessGame = new ChessGame();
        dataAccess.saveGame(new GameData(gameID, username1, username2, gameName, chessGame));

        // Test getGame()
        GameData game = dataAccess.getGame(gameID);
        assertEquals(GameData.class, game.getClass());
    }

    @Test
    void getGames() throws SQLException, DataAccessException {
        // Create users
        String username1 = "user1";
        String username2 = "user2";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));
        dataAccess.saveUser(new UserData(username2, password, email));

        // Create games
        dataAccess.saveGame(new GameData(1, username1, username2, "game1", new ChessGame()));
        dataAccess.saveGame(new GameData(2, username1, username2, "game2", new ChessGame()));
        dataAccess.saveGame(new GameData(3, username1, username2, "game3", new ChessGame()));
        dataAccess.saveGame(new GameData(4, username1, username2, "game4", new ChessGame()));
        dataAccess.saveGame(new GameData(5, username1, username2, "game5", new ChessGame()));

        // Test getGames()
        ArrayList<GameData> games = dataAccess.getGames();
        assertEquals(5, games.size());
        for (var game : games) {
            assertEquals(GameData.class, game.getClass());
        }
    }

    @Test
    void saveGame() throws SQLException, DataAccessException {
        // Create users
        String username1 = "user1";
        String username2 = "user2";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));
        dataAccess.saveUser(new UserData(username2, password, email));

        // Save game
        int gameID = 1;
        String gameName = "game1";
        ChessGame chessGame = new ChessGame();
        dataAccess.saveGame(new GameData(gameID, username1, username2, gameName, chessGame));

        // Test saveGame()
        GameData game = dataAccess.getGame(gameID);
        assertEquals(username1, game.whiteUsername());
        assertEquals(username2, game.blackUsername());
        assertEquals(gameName, game.gameName());
        assertEquals(chessGame, game.game());
    }

    @Test
    void getAuth() throws SQLException, DataAccessException {
        // Create user
        String username1 = "user1";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));

        // Create auth
        String authToken = "uuid-uuid-uuid-uuid";
        dataAccess.saveAuth(new AuthData(authToken, username1));

        // Test getAuth()
        AuthData auth = dataAccess.getAuth(authToken);
        assertEquals(AuthData.class, auth.getClass());
    }

    @Test
    void saveAuth() throws SQLException, DataAccessException {
        // Create user
        String username1 = "user1";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));

        // Create auth
        String authToken = "uuid-uuid-uuid-uuid";
        dataAccess.saveAuth(new AuthData(authToken, username1));

        // Test getAuth()
        AuthData auth = dataAccess.getAuth(authToken);
        assertEquals(authToken, auth.authToken());
        assertEquals(username1, auth.username());
    }

    @Test
    void deleteAuth() throws SQLException, DataAccessException {
        // Create user
        String username1 = "user1";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));

        // Create auth
        String authToken = "uuid-uuid-uuid-uuid";
        dataAccess.saveAuth(new AuthData(authToken, username1));

        // Test getAuth()
        dataAccess.deleteAuth(authToken);
        assertNull(dataAccess.getAuth(authToken));
    }

    @Test
    void getUser() throws SQLException, DataAccessException {
        // Create user
        String username1 = "user1";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));

        // Test getUser()
        UserData user = dataAccess.getUser(username1);
        assertEquals(UserData.class, user.getClass());
    }

    @Test
    void saveUser() throws SQLException, DataAccessException {
        // Create user
        String username1 = "user1";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));

        // Test getUser()
        UserData user = dataAccess.getUser(username1);
        assertEquals(username1, user.username());
        assertEquals(password, user.password());
        assertEquals(email, user.email());
    }

    @Test
    void clear() throws SQLException, DataAccessException {
        dataAccess.clear();

        // Create users
        String username1 = "user1";
        String username2 = "user2";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));
        dataAccess.saveUser(new UserData(username2, password, email));

        // Save games
        dataAccess.saveGame(new GameData(1, username1, username2, "game1", new ChessGame()));
        dataAccess.saveGame(new GameData(2, username1, username2, "game2", new ChessGame()));

        // Save auths
        dataAccess.saveAuth(new AuthData("uuid-uuid-uuid-uu1d", username1));
        dataAccess.saveAuth(new AuthData("uuid-uuid-uuid-uu2d", username2));

        // Test clear()
        dataAccess.clear();
        assertNull(dataAccess.getUser(username1));
        assertNull(dataAccess.getUser(username2));
        assertNull(dataAccess.getGame(1));
        assertNull(dataAccess.getGame(2));
        assertNull(dataAccess.getAuth("uuid-uuid-uuid-uu1d"));
        assertNull(dataAccess.getAuth("uuid-uuid-uuid-uu2d"));
    }
}
