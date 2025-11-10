package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("https", "localhost", 8080);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest() {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        assertDoesNotThrow(() -> serverFacade.register(username, password, email));
    }

    @Test
    public void logoutTest() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);

        assertDoesNotThrow(() -> serverFacade.logout());
    }

    @Test
    public void loginTest() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);
        serverFacade.logout();

        assertDoesNotThrow(() -> serverFacade.login(username, password));
    }

    @Test
    public void createGame() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);

        var gameName = "game1";
        serverFacade.createGame(gameName);
        assertNotEquals(0, serverFacade.getGames().size());
    }

    @Test
    public void getGames() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);

        var gameName = "game1";
        var gameName2 = "game2";
        serverFacade.createGame(gameName);
        serverFacade.createGame(gameName2);

        assertEquals(2, serverFacade.getGames().size());
    }

    @Test
    public void joinGame() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);

        var gameName = "game1";
        serverFacade.createGame(gameName);

        serverFacade.joinGame(1, ChessGame.TeamColor.WHITE);

        var games = serverFacade.getGames();
        assertEquals(username, games.getFirst().whiteUsername());
    }
}
