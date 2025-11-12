package ui;

import chess.ChessGame;
import serverfacade.Client;
import serverfacade.GameData;
import serverfacade.ServerFacade;

import java.util.*;

public class CLI {
    Scanner scanner;
    ServerFacade server;
    boolean isAuthorized = false;
    HashMap<String, CLIRunnable> handlers;
    ArrayList<GameData> games;
    int observeGameID;

    public CLI() {
        scanner = new Scanner(System.in);
        server = new ServerFacade("http", "localhost", 8080);
        games = new ArrayList<>();
    }

    private int getGameNumber(int gameID) {
        for (var game : games) {
            if (game.gameID() == gameID) {
                return games.indexOf(game) + 1;
            }
        }
        return -1;
    }

    public void help() {
        if (isAuthorized) {
            System.out.print("""
                    |-----------COMMAND------------:-----------INFO-----------|
                    | create <game_name>           : Creates a new game       |
                    | list                         : List all games           |
                    | join <game_id> [WHITE|BLACK] : Play in a specified game |
                    | observe <game_id>            : Observe a specified game |
                    | logout                       : Log out of your account  |
                    | quit                         : Quit the game            |
                    | help                         : Display help message     |""");
        } else {
            System.out.print("""
                    |----------------COMMAND-----------------:------------INFO------------|
                    | register <username> <password> <email> : Register for a new account |
                    | login <username> <password>            : Sign into your account     |
                    | quit                                   : Quit the game              |
                    | help                                   : Display help message       |""");

        }

    }

    public void quit() {
        System.out.println("Goodbye!");
    }

    public void login(String[] args) throws Exception {
        if (isAuthorized) {
            System.out.println("Already logged in");
            return;
        } else if (args.length < 2) {
            System.out.println("Must provide username and password");
            return;
        }

        String username = args[0];
        String password = args[1];

        try {
            if (username.isBlank() || password.isBlank()) {
                System.out.println("Password and username are required fields.");
            }
            server.login(username, password);
            isAuthorized = true;
        } catch (Client.BadRequestResponse e) {
            System.out.println("Username and password are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void register(String[] args) throws Exception {
        if (isAuthorized) {
            System.out.println("Already logged in");
            return;
        } else if (args.length < 3) {
            System.out.println("Must provide username, password, and email");
            return;
        }

        String username = args[0];
        String password = args[1];
        String email = args[2];

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
        if (!isAuthorized) {
            System.out.println("Not logged in");
            return;
        }
        server.logout();
        isAuthorized = false;
    }

    public void create(String[] args) throws Exception {
        if (!isAuthorized) {
            System.out.println("Not logged in");
            return;
        } else if (args.length == 0) {
            System.out.println("Must provide game name");
            return;
        }

        String gameName = args[0];

        try {
            int gameID = server.createGame(gameName);
            games = server.getGames();
            System.out.printf("Game created with id %s%n", getGameNumber(gameID));
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game name");
        }
    }

    public void list() throws Exception {
        if (!isAuthorized) {
            System.out.println("Not logged in");
            return;
        }

        games = server.getGames();
        System.out.println("Game : White : Black");
        for (int i = 0; i < games.size(); i++) {
            var game = games.get(i);
            System.out.printf("%d) %s : %s : %s%n",
                    i + 1, game.gameName(),
                    game.whiteUsername() == null ? "none" : game.whiteUsername(),
                    game.blackUsername() == null ? "none": game.blackUsername());
        }
    }

    public void join(String[] args) throws Exception {
        if (!isAuthorized) {
            System.out.println("Not logged in");
            return;
        } else if (args.length < 2) {
            System.out.println("Must provide game ID and color");
            return;
        }

        games = server.getGames();

        try {
            int gameNumber = Integer.parseInt(args[0]);
            int gameID = games.get(gameNumber - 1).gameID();

            String colorString = args[1];
            ChessGame.TeamColor color = colorString.equalsIgnoreCase("white")
                    ? ChessGame.TeamColor.WHITE
                    : ChessGame.TeamColor.BLACK;

            server.joinGame(gameID, color);
            BoardView view = new BoardView(games.get(gameNumber - 1).game(), color);
            System.out.print(view.render());
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid game ID");
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game ID or color");
        } catch (Client.ForbiddenResponse e) {
            System.out.println("Position already filled");
        }
    }

    public void observe(String[] args) throws Exception {
        if (!isAuthorized) {
            System.out.println("Not logged in");
            return;
        } else if (args.length == 0) {
            System.out.println("Must provide game ID");
            return;
        }

        try {
            games = server.getGames();
            int gameNumber = Integer.parseInt(args[0]);
            BoardView view = new BoardView(games.get(gameNumber - 1).game(), ChessGame.TeamColor.WHITE);
            System.out.print(view.render());
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID");
        }
    }

    public interface CLIRunnable {
        void run(String[] args) throws Exception;
    }

    public boolean processCommand(String command) {
        handlers = new HashMap<>();
        handlers.put("help", args -> help());
        handlers.put("quit", args -> quit());
        handlers.put("login", this::login);
        handlers.put("register", this::register);
        handlers.put("logout", args -> logout());
        handlers.put("create", this::create);
        handlers.put("list", args -> list());
        handlers.put("join", this::join);
        handlers.put("observe", this::observe);

        try {
            String[] commandArray = command.split("\\s+");
            if (commandArray.length == 0) {
                return true;
            }

            String operation = commandArray[0];
            if (handlers.containsKey(operation)) {
                handlers.get(operation).run(Arrays.copyOfRange(commandArray, 1, commandArray.length));
            } else {
                System.out.println("Not a valid command");
            }
        } catch (Exception e) {
            System.out.println("An error occurred and the operation was unsuccessful.");
        }

        return !command.equals("quit");
    }
}
