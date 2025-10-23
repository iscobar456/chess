import chess.ChessGame;
import dataaccess.AuthData;
import dataaccess.GameData;
import dataaccess.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.Test;
import service.Service;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private final Service service = new Service();

    @Test
    void register() {
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
        AuthData auth = service.register(username, password, email);
        service.validateToken(auth.authToken());
    }

    @Test
    void login() {
        String username = "testuser00";
        String password = "strongpassword00";
        service.clear();
        AuthData auth = service.register(username, password, "testuser@test.com");
        service.deleteAuth(auth.authToken());

        assertThrows(BadRequestResponse.class, () -> {service.login(null, null);});
        assertThrows(BadRequestResponse.class, () -> {service.login(null, password);});
        assertThrows(BadRequestResponse.class, () -> {service.login(username, null);});
        assertThrows(UnauthorizedResponse.class, () -> {service.login(username.substring(1), password);});
        assertThrows(UnauthorizedResponse.class, () -> {service.login(username, password.substring(1));});
        auth = service.login(username, password);
        service.validateToken(auth.authToken());
    }

    @Test
    void deleteAuth() {
        service.clear();
        AuthData auth = service.register("testuser00", "strongpassword00", "testuser@test.com");
        service.deleteAuth(auth.authToken());
        assertThrows(UnauthorizedResponse.class, () -> {service.validateToken(auth.authToken());});
    }

    @Test
    void validateToken() {
        service.clear();
        AuthData auth = service.register("testuser00", "strongpassword00", "testuser@test.com");
        assertDoesNotThrow(() -> {service.validateToken(auth.authToken());});
        assertThrows(UnauthorizedResponse.class, () -> {service.validateToken(auth.authToken().substring(1));});
    }

    @Test
    void joinGame() {
        service.clear();
        int gameID = service.createGame("game1");

        // Add black user successfully.
        service.joinGame(gameID, "testuser00", ChessGame.TeamColor.BLACK);
        GameData game = service.getGame(gameID);
        assertEquals("testuser00", game.blackUsername());
        assertEquals(null, game.whiteUsername());

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
        assertThrows(ForbiddenResponse.class, () -> {
            service.joinGame(gameID, "testuser02", ChessGame.TeamColor.BLACK);
        });
    }

    @Test
    void joinFullGameWhite() {
        service.clear();
        int gameID = service.createGame("game1");
        service.joinGame(gameID, "testuser00", ChessGame.TeamColor.BLACK);
        service.joinGame(gameID, "testuser01", ChessGame.TeamColor.WHITE);
        assertThrows(ForbiddenResponse.class, () -> {
            service.joinGame(gameID, "testuser02", ChessGame.TeamColor.WHITE);
        });
    }

    @Test
    void joinGameBadID() {
        service.clear();
        int gameID = service.createGame("game1");
        assertThrows(BadRequestResponse.class, () -> {
            service.joinGame(gameID + 1, "testuser00", ChessGame.TeamColor.BLACK);
        });
    }

    @Test
    void createGame() {
//        GameData game1 = new GameData(1, "testuser00", "testuser01", "game1", new ChessGame());
//        GameData game2 = new GameData(2, "testuser02", "testuser03", "game2", new ChessGame());
//        GameData game3 = new GameData(3, "testuser04", "testuser05", "game3", new ChessGame());
        service.clear();
        int gameID = service.createGame("game1");
        ArrayList<GameData> games = service.getGames();
        assertEquals(1, games.size());
        assertEquals(gameID, games.getFirst().gameID());
        assertEquals(null, games.getFirst().whiteUsername());
        assertEquals(null, games.getFirst().blackUsername());
        assertEquals("game1", games.getFirst().gameName());

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
    void clear() {
        AuthData auth = new AuthData("asdjjjdjjd2978373881", "testuser00");
        UserData user = new UserData("testuser00", "strongpassword", "testuser@example.com");
        GameData game = new GameData(4, "testuser00", "testuser01", "game1", new ChessGame());
        service.dataAccess.saveUser(user);
        service.dataAccess.saveAuth(auth);
        service.dataAccess.saveGame(game);

        assertFalse(service.dataAccess.users.isEmpty());
        assertFalse(service.dataAccess.auths.isEmpty());
        assertFalse(service.dataAccess.games.isEmpty());

        service.clear();

        assertTrue(service.dataAccess.users.isEmpty());
        assertTrue(service.dataAccess.auths.isEmpty());
        assertTrue(service.dataAccess.games.isEmpty());
    }
}
