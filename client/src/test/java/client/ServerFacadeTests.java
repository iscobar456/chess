package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.Client;
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
        serverFacade = new ServerFacade("http", "localhost", 8080);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        serverFacade.clear();
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
        assertTrue(serverFacade.getIsAuthorized());
    }

    @Test
    public void registerTestMissingArg() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = " ";
        assertThrows(Client.BadRequestResponse.class, () -> serverFacade.register(username, password, email));
    }

    @Test
    public void logoutTest() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);

        assertDoesNotThrow(() -> serverFacade.logout());
        assertFalse(serverFacade.getIsAuthorized());
    }

    @Test
    public void logoutNotLoggedIn() throws Exception {
        assertThrows(Client.UnauthorizedResponse.class, () -> serverFacade.logout());
    }

    @Test
    public void loginTest() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);
        serverFacade.logout();

        serverFacade.login(username, password);
        assertTrue(serverFacade.getIsAuthorized());
    }

    @Test
    public void loginMissingInput() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);
        serverFacade.logout();

        assertThrows(Client.BadRequestResponse.class, () -> serverFacade.login(username, "  "));
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
    public void createGameMissingName() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);

        assertThrows(Client.BadRequestResponse.class, () -> serverFacade.createGame("  "));
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
    public void getGamesUnauthorized() throws Exception {
        assertThrows(Client.UnauthorizedResponse.class, () -> serverFacade.getGames());
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

    @Test
    public void joinGameInvalidID() throws Exception {
        var username = "isaac";
        var password = "strongpassword";
        var email = "test@test.com";
        serverFacade.register(username, password, email);

        var gameName = "game1";
        serverFacade.createGame(gameName);

        assertThrows(Client.BadRequestResponse.class, () -> serverFacade.joinGame(2, ChessGame.TeamColor.WHITE));
    }
}
