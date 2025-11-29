package ui;

import chess.*;
import data.GameData;
import data.Update;
import serverfacade.*;

import java.util.*;

public class CLI implements UpdateListener {
    Scanner scanner;
    ServerFacade server;
    boolean isAuthorized = false;
    HashMap<String, CLIRunnable> handlers;
    ArrayList<GameData> games;
    GameData observedGame;
    ChessGame.TeamColor perspective;
    boolean inGameMode;
    GameModeManager gameModeView;
    String user;

    public CLI() throws Exception {
        scanner = new Scanner(System.in);
        server = new ServerFacade("http", "localhost", 8080, this);
        games = new ArrayList<>();
        handlers = constructHandlerMap();
        inGameMode = false;
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
        if (isAuthorized && !inGameMode) {
            System.out.print("""
                    |-----------COMMAND------------:-----------INFO-----------|
                    | create <game_name>           : Creates a new game       |
                    | list                         : List all games           |
                    | join <game_id> [WHITE|BLACK] : Play in a specified game |
                    | observe <game_id>            : Observe a specified game |
                    | logout                       : Log out of your account  |
                    | quit                         : Quit the game            |
                    | help                         : Display help message     |""");
        } else if (isAuthorized && inGameMode) {
            System.out.print("""
                    |-----------COMMAND------------:-----------INFO-----------|
                    | create <game_name>           : Creates a new game       |
                    | list                         : List all games           |
                    | move <start> <end> <promote> : Move a piece             |
                    | resign                       : Forfeit the game         |
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
        if (args.length < 2) {
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
            user = username;
            System.out.printf("Logged in as %s%n", username);
        } catch (Client.BadRequestResponse e) {
            System.out.println("Username and password are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void register(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Must provide username, password, and email");
            return;
        }

        String username = args[0];
        String password = args[1];
        String email = args[2];

        try {
            server.register(username, password, email);
            user = username;
            this.isAuthorized = true;
            System.out.printf("Logged in as %s%n", username);
        } catch (Client.BadRequestResponse e) {
            System.out.println("Username, password, and email are required fields.");
        } catch (Client.UnauthorizedResponse e) {
            System.out.println("Incorrect username or password.");
        }
    }

    public void logout() throws Exception {
        server.logout();
        isAuthorized = false;
        user = null;
        System.out.println("Logged out");
    }

    public void create(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Must provide game name");
            return;
        }

        String gameName = args[0];

        try {
            int gameID = server.createGame(gameName);
            System.out.printf("Game created with id %s%n", getGameNumber(gameID));
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game name");
        }
    }

    public void list() throws Exception {
        System.out.println("Game : White : Black");
        for (int i = 0; i < games.size(); i++) {
            var game = games.get(i);
            System.out.printf("%d) %s : %s : %s%n",
                    i + 1, game.gameName(),
                    game.whiteUsername() == null ? "none" : game.whiteUsername(),
                    game.blackUsername() == null ? "none" : game.blackUsername());
        }
    }

    public void join(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Must provide game ID and color");
            return;
        }


        try {
            int gameNumber = Integer.parseInt(args[0]);
            int gameID = games.get(gameNumber - 1).gameID();

            String colorString = args[1];
            ChessGame.TeamColor color;
            if (colorString.equalsIgnoreCase("white")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (colorString.equalsIgnoreCase("black")) {
                color = ChessGame.TeamColor.WHITE;
            } else {
                System.out.println("Color must be white or black");
                return;
            }

            server.joinGame(gameID, color);
            setObservedGame(Integer.parseInt(args[0]) - 1);
            inGameMode = true;
            gameModeView = new GameModeManager(color, false);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid game ID");
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game ID or color");
        } catch (Client.ForbiddenResponse e) {
            System.out.println("Position already filled");
        }
    }

    public void observe(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Must provide game ID");
            return;
        }

        try {
            setObservedGame(Integer.parseInt(args[0]) - 1);
            inGameMode = true;
            gameModeView = new GameModeManager(ChessGame.TeamColor.WHITE, true);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Invalid game ID");
        }
    }


    private void makeMove(String[] args) {
        if (args.length < 2) {
            gameModeView.notify("Must provide a starting and ending position. (e.g., B2 C2)");
            return;
        }

        try {
            ChessMove move = new ChessMove(
                    new ChessPosition(args[0]),
                    new ChessPosition(args[1]),
                    args.length > 2
                            ? ChessPiece.stringToType(args[3])
                            : null
            );
            observedGame.game().makeMove(move);
            server.makeMove(move);
        } catch (Exception e) {
            gameModeView.notify("Invalid move. Must provide a valid starting, ending position, " +
                    "and a promotion piece if applicable. (e.g., B3 A4 QUEEN)");
        }
    }

    private void setObservedGame(int id) throws IndexOutOfBoundsException {
        observedGame = games.get(id);
    }

    @Override
    public void onNotification(String notification) {
        gameModeView.notify(notification);
    }

    @Override
    public void onLoadGame(GameData gameData) {
        int updateGameId = gameData.gameID();
        int gameNumber = getGameNumber(updateGameId);
        if (gameNumber == -1) {
            return;
        }

        GameData localGameData = games.get(gameNumber);

        // If the updated game is the observed game, re-render.
        if (localGameData.game().equals(observedGame)) {
            games.set(gameNumber, gameData);
            observedGame = gameData;
            gameModeView.renderGame(observedGame.game());
        }
    }

    @Override
    public void onDisconnect() {
        inGameMode = false;
        gameModeView.exitGameMode();
    }

    public interface CLIRunnable {
        void run(String[] args) throws Exception;
    }

    public void handleAuthorization(String operation) throws Exception {
        if (operation.equalsIgnoreCase("login")
                || operation.equalsIgnoreCase("register")) {
            if (isAuthorized) {
                throw new Exception("Already logged in");
            }
        } else {
            if (!isAuthorized) {
                throw new Exception("Not logged in");
            }
        }
    }

    private HashMap<String, CLIRunnable> constructHandlerMap() {
        HashMap<String, CLIRunnable> handlerMap = new HashMap<>();
        handlerMap.put("help", args -> help());
        handlerMap.put("quit", args -> quit());
        handlerMap.put("login", this::login);
        handlerMap.put("register", this::register);
        handlerMap.put("logout", args -> logout());
        handlerMap.put("create", this::create);
        handlerMap.put("list", args -> list());
        handlerMap.put("join", this::join);
        handlerMap.put("observe", this::observe);
        handlerMap.put("move", this::makeMove);
        return handlerMap;
    }

    public boolean processCommand(String command) {

        String[] commandArray = command.split("\\s+");
        if (commandArray.length == 0) {
            return true;
        }
        String operation = commandArray[0].toLowerCase(Locale.ROOT);
        if (!handlers.containsKey(operation)) {
            System.out.println("Not a valid command");
            return true;
        }

        try {
            handleAuthorization(operation);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }

        try {
            if (operation.equals("create")
                    || operation.equals("list")
                    || operation.equals("join")
                    || operation.equals("observe")) {
                games = server.getGames();
            }
            handlers.get(operation).run(Arrays.copyOfRange(commandArray, 1, commandArray.length));
        } catch (Exception e) {
            System.out.println("An error occurred and the operation was unsuccessful.");
        }

        if (inGameMode) {
            gameModeView.renderGame(observedGame.game());
        }

        return !command.equals("quit");
    }
}
