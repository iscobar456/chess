package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("https", "localhost", 8080);
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
        serverFacade.register(username, password, email);
    }

    @Test
    public void loginTest() {
        var username = "isaac";
        var password = "strongpassword";
        serverFacade.login(username, password);
    }
}
