package dataaccess;

import java.util.ArrayList;
import io.javalin.http.InternalServerErrorResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import chess.ChessGame;

public class DataAccessTests {
    private final SQLDataAccess dataAccess = new SQLDataAccess();

    @Test
    void getGame() {
        dataAccess.clear();

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
    void getGameInvalidId() {
        dataAccess.clear();

        // Create game
        int gameID = 1;
        String gameName = "game1";
        ChessGame chessGame = new ChessGame();
        dataAccess.saveGame(new GameData(gameID, null, null, gameName, chessGame));

        // Test getGame()
        assertNull(dataAccess.getGame(2));
    }

    @Test
    void getGames() {
        dataAccess.clear();

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
    void getGamesNoGames() {
        dataAccess.clear();

        // Test getGames()
        ArrayList<GameData> games = dataAccess.getGames();
        assertTrue(games.isEmpty());
    }

    @Test
    void saveGame() {
        dataAccess.clear();

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
    void saveGameInvalidUsername() {
        dataAccess.clear();

        // Save game
        assertThrows(InternalServerErrorResponse.class, () -> dataAccess.saveGame(new GameData(1,
                "username",
                null,
                "name",
                new ChessGame())));
    }

    @Test
    void getAuth() {
        dataAccess.clear();

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
    void getAuthInvalidToken() {
        dataAccess.clear();

        // Create user
        String username1 = "user1";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));

        // Create auth
        String authToken = "uuid-uuid-uuid-uuid";
        dataAccess.saveAuth(new AuthData(authToken, username1));

        // Test getAuth()
        assertNull(dataAccess.getAuth(authToken.substring(1)));
    }

    @Test
    void saveAuth() {
        dataAccess.clear();

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
    void saveAuthInvalidUsername() {
        dataAccess.clear();

        // Save auth
        assertThrows(InternalServerErrorResponse.class, () -> dataAccess.saveAuth(
                new AuthData("uuid", "username")));
    }

    @Test
    void deleteAuth() {
        dataAccess.clear();

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
    void deleteAuthInvalidToken() {
        dataAccess.clear();
        dataAccess.saveUser(new UserData("username1", "weakpassword", "test@mail.com"));
        dataAccess.saveAuth(new AuthData("uuid", "username1"));

        // Delete auth
        dataAccess.deleteAuth("uuid-invalid");
        assertNotNull(dataAccess.getAuth("uuid"));
    }

    @Test
    void getUser() {
        dataAccess.clear();

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
    void getUserInvalidUsername() {
        dataAccess.clear();

        // Create user
        String username1 = "user1";
        String password = "weakpassword";
        String email = "user1@mail.com";
        dataAccess.saveUser(new UserData(username1, password, email));

        // Test getUser()
        assertNull(dataAccess.getUser(username1.substring(1)));
    }

    @Test
    void saveUser() {
        dataAccess.clear();

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
    void saveUserInvalidInput() {
        assertThrows(InternalServerErrorResponse.class, () -> {
            dataAccess.saveUser(new UserData(null, null, null));
            dataAccess.saveUser(new UserData(null, null, null));
        });
    }

    @Test
    void clear() {
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
