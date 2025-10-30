package service;

import chess.ChessGame;
import dataaccess.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class ServiceTests {
    private final SQLDataAccess dataAccess = new SQLDataAccess();
    private final Service service = new Service(dataAccess);

    @Test
    void register() {
        String username = "testuser00";
        String password = "strongpassword00";
        String email = "testuser@test.com";
        service.clear();
        AuthData auth = service.register(username, password, email);
        service.validateToken(auth.authToken());
    }

    @Test
    void registerInvalidInput() {
        String username = "testuser00";
        String password = "strongpassword00";
        String email = "testuser@test.com";
        service.clear();
        assertThrows(BadRequestResponse.class, () -> service.register(null, null, null));
        assertThrows(BadRequestResponse.class, () -> service.register(null, null, email));
        assertThrows(BadRequestResponse.class, () -> service.register(null, password, null));
        assertThrows(BadRequestResponse.class, () -> service.register(null, password, email));
        assertThrows(BadRequestResponse.class, () -> service.register(username, null, null));
        assertThrows(BadRequestResponse.class, () -> service.register(username, null, email));
        assertThrows(BadRequestResponse.class, () -> service.register(username, password, null));
    }

    @Test
    void login() {
        String username = "testuser00";
        String password = "strongpassword00";
        service.clear();
        AuthData auth = service.register(username, password, "testuser@test.com");
        service.deleteAuth(auth.authToken());
        assertDoesNotThrow(() -> service.login(username, password));
    }

    @Test
    void loginInvalidInput() {
        String username = "testuser00";
        String password = "strongpassword00";
        service.clear();
        AuthData auth = service.register(username, password, "testuser@test.com");
        service.deleteAuth(auth.authToken());
        assertThrows(BadRequestResponse.class, () -> service.login(null, null));
        assertThrows(BadRequestResponse.class, () -> service.login(null, password));
        assertThrows(BadRequestResponse.class, () -> service.login(username, null));
        assertThrows(UnauthorizedResponse.class, () -> service.login(username.substring(1), password));
        assertThrows(UnauthorizedResponse.class, () -> service.login(username, password.substring(1)));
    }

    @Test
    void createAuth() {
        service.clear();
        AuthData auth = service.createAuth("testuser00");
        assertNotNull(auth.authToken());
    }

    @Test
    void createAuthInvalid() {
        service.clear();
        assertThrows(BadRequestResponse.class, () -> service.createAuth(null));
    }

    @Test
    void deleteAuth() {
        service.clear();
        AuthData auth = service.register("testuser00", "strongpassword00", "testuser@test.com");
        service.deleteAuth(auth.authToken());
        assertThrows(UnauthorizedResponse.class, () -> service.validateToken(auth.authToken()));
    }

    @Test
    void deleteAuthInvalid() {
        service.clear();
        AuthData auth = service.register("testuser00", "strongpassword00", "testuser@test.com");
        service.deleteAuth(auth.authToken().substring(1));
        assertDoesNotThrow(() -> service.validateToken(auth.authToken()));
    }

    @Test
    void validateToken() {
        service.clear();
        AuthData auth = service.register("testuser00", "strongpassword00", "testuser@test.com");
        assertDoesNotThrow(() -> {service.validateToken(auth.authToken());});
    }

    @Test
    void validateTokenInvalid() {
        service.clear();
        AuthData auth = service.register("testuser00", "strongpassword00", "testuser@test.com");
        assertThrows(UnauthorizedResponse.class, () -> service.validateToken(auth.authToken().substring(1)));
    }

    @Test
    void joinGame() {
        service.clear();
        int gameID = service.createGame("game1");

        // Add black user successfully.
        service.joinGame(gameID, "testuser00", ChessGame.TeamColor.BLACK);
        GameData game = service.getGame(gameID);
        assertEquals("testuser00", game.blackUsername());
        assertNull(game.whiteUsername());

        // Add white user successfully.
        service.joinGame(gameID, "testuser01", ChessGame.TeamColor.WHITE);
        game = service.getGame(gameID);
        assertEquals("testuser01", game.whiteUsername());
        assertEquals("testuser00", game.blackUsername());
    }

    @Test
    void joinFullGameBlack() {
        service.clear();
        int gameID = service.createGame("game1");
        service.joinGame(gameID, "testuser00", ChessGame.TeamColor.BLACK);
        service.joinGame(gameID, "testuser01", ChessGame.TeamColor.WHITE);
        assertThrows(ForbiddenResponse.class, () -> service.joinGame(gameID, "testuser02", ChessGame.TeamColor.BLACK));
    }

    @Test
    void joinFullGameWhite() {
        service.clear();
        int gameID = service.createGame("game1");
        service.joinGame(gameID, "testuser00", ChessGame.TeamColor.BLACK);
        service.joinGame(gameID, "testuser01", ChessGame.TeamColor.WHITE);
        assertThrows(ForbiddenResponse.class, () -> service.joinGame(gameID, "testuser02", ChessGame.TeamColor.WHITE));
    }

    @Test
    void joinGameBadID() {
        service.clear();
        int gameID = service.createGame("game1");
        assertThrows(BadRequestResponse.class, () -> service.joinGame(gameID + 1, "testuser00", ChessGame.TeamColor.BLACK));
    }

    @Test
    void createGame() {
        service.clear();
        int gameID = service.createGame("game1");
        ArrayList<GameData> games = service.getGames();
        assertEquals(1, games.size());
        assertEquals(gameID, games.getFirst().gameID());
        assertNull(games.getFirst().whiteUsername());
        assertNull(games.getFirst().blackUsername());
        assertEquals("game1", games.getFirst().gameName());

    }

    @Test
    void createGameNullName() {
        service.clear();
        assertThrows(BadRequestResponse.class, () -> service.createGame(null));
    }

    @Test
    void getGames() {
        service.clear();
        service.createGame("game1");
        service.createGame("game2");
        service.createGame("game3");
        assertEquals(3, service.getGames().size());
    }

    @Test
    void getGamesNoDataAccess() {
        service.dataAccess = null;
        assertThrows(NullPointerException.class, service::getGames);
        service.dataAccess = new MemoryDataAccess();
    }

    @Test
    void clear() {
        String authToken = "asdjjjdjjd2978373881";
        String username = "testuser00";
        AuthData auth = new AuthData(authToken, username);
        UserData user = new UserData(username, "strongpassword", "testuser@example.com");
        GameData game = new GameData(4, username, "testuser01", "game1", new ChessGame());
        dataAccess.saveUser(user);
        dataAccess.saveAuth(auth);
        dataAccess.saveGame(game);

        assertNotNull(dataAccess.getUser(username));
        assertNotNull(dataAccess.getAuth(authToken));
        assertNotNull(dataAccess.getGame(4));

        service.clear();

        assertNull(dataAccess.getUser(username));
        assertNull(dataAccess.getAuth(authToken));
        assertNull(dataAccess.getGame(4));
    }

    @Test
    void clearNoDataAccess() {
        service.dataAccess = null;
        assertThrows(NullPointerException.class, service::clear);
        service.dataAccess = new MemoryDataAccess();
    }
}
