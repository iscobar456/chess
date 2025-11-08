package ui;

import com.google.gson.reflect.TypeToken;
import serverfacade.Client;
import serverfacade.ServerFacade;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CLI {
    Scanner scanner;
    ServerFacade server;
    boolean isAuthorized = false;
    HashMap<String, CLIRunnable> handlers;
    ArrayList<Map<String, Object>> games;
    int observeGameID;

    public CLI() {
        scanner = new Scanner(System.in);
        server = new ServerFacade("http", "localhost", 8080);
        games = new ArrayList<>();
    }

    private int getGameNumber(int gameID) {
        for (var game : games) {
            if ((int) game.get("gameID") == gameID) {
                return games.indexOf(game) + 1;
            }
        }
        return -1;
    }

    public void help() {
        if (isAuthorized) {
            System.out.printf("""
                    logout: Log out of your account
                    create: Creates a new game
                    list: List all games
                    play: Play in a specified game
                    observe: Observe a specified game
                    quit: Quit the game
                    help: Display help message""");
        } else {
            System.out.printf("""
                    register: Register for a new account
                    login: Sign into your account
                    quit: Quit the game
                    help: Display help message""");

        }

    }

    public void quit() {
        System.out.println("Goodbye!");
    }

    public void login() throws Exception {
        System.out.printf("Username: ");
        String username = scanner.nextLine();
        System.out.printf("Password: ");
        String password = scanner.nextLine();

        try {
            server.login(username, password);
            isAuthorized = true;
        } catch (Client.BadRequestResponse e) {
            System.out.println("Password and username are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void register() throws Exception {
        System.out.printf("Username: ");
        String username = scanner.nextLine();
        System.out.printf("Password: ");
        String password = scanner.nextLine();
        System.out.printf("Email: ");
        String email = scanner.nextLine();

        try {
            server.register(username, password, email);
            this.isAuthorized = true;
        } catch (Client.BadRequestResponse e) {
            System.out.println("Username, password, and email are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void logout() throws Exception {
        try {
            server.logout();
            isAuthorized = false;
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Not logged in.");
        }
    }

    public void create() throws Exception {
        System.out.printf("Game name: ");
        String gameName = scanner.nextLine();

        try {
            int gameID = server.createGame(gameName);
            System.out.printf("Game created with id %s%n", getGameNumber(gameID));
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game name");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Not logged in");
        }
    }

    public void list() throws Exception {
        try {
            ArrayList<Map<String, Object>> games = server.listGames();
            this.games = games;
            System.out.println("Game : White : Black");
            for (int i = 0; i < games.size(); i++) {
                var game = games.get(i);
                System.out.printf("%d) %s : %s : %s%n",
                        i + 1, game.get("gameName"),
                        game.getOrDefault("whiteUsername", "None"),
                        game.getOrDefault("blackUsername", "None"));
            }
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Not logged in");
        }
    }

    public void join() throws Exception {
        System.out.printf("Game ID: ");
        int gameNumber = scanner.nextInt();
        int gameID = ((Double) games.get(gameNumber-1).get("gameID")).intValue();

        try {
            server.joinGame(gameID);
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game ID");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Not logged in");
        }
    }

    public void observe() {
        System.out.printf("Game ID: ");
        int gameID = scanner.nextInt();
        observeGameID = gameID;
    }

    public interface CLIRunnable {
        void run() throws Exception;
    }

    public boolean processCommand(String command) {
        handlers = new HashMap<>();
        handlers.put("help", () -> help());
        handlers.put("quit", () -> quit());
        handlers.put("login", () -> login());
        handlers.put("register", () -> register());
        if (isAuthorized) {
            handlers.put("logout", () -> logout());
            handlers.put("create", () -> create());
            handlers.put("list", () -> list());
            handlers.put("join", () -> join());
            handlers.put("observe", () -> observe());
        }

        try {
            handlers.get(command).run();
        } catch (Exception e) {
            System.out.println("An error occurred and the operation was unsuccessful.");
        }

        return !command.equals("quit");
    }
}
