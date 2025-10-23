import chess.ChessGame;
import dataaccess.AuthData;
import dataaccess.GameData;
import dataaccess.UserData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private final Service service = new Service();

    @Test
    void createGame() {
//        GameData game1 = new GameData(1, "testuser00", "testuser01", "game1", new ChessGame());
//        GameData game2 = new GameData(2, "testuser02", "testuser03", "game2", new ChessGame());
//        GameData game3 = new GameData(3, "testuser04", "testuser05", "game3", new ChessGame());
        service.clear();
        service.createGame("game1");
        ArrayList<GameData> games = service.getGames();
        assertEquals(1, games.size());
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
