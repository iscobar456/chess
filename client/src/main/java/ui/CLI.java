package ui;

import chess.*;
import data.GameData;
import serverfacade.*;

import java.util.*;

public class CLI implements UpdateListener {
    Scanner scanner;
    ServerFacade server;
    boolean isAuthorized = false;
    ArrayList<GameData> games;
    GameData observedGame;
    boolean inGameMode;
    GameModeManager gameModeView;
    String user;

    public CLI() throws Exception {
        scanner = new Scanner(System.in);
        server = new ServerFacade("http", "localhost", 8080, this);
        games = new ArrayList<>();
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
        } else if (isAuthorized) {
            gameModeView.printHelp();
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
            games = server.getGames();
            System.out.printf("Game created with id %s%n", getGameNumber(gameID));
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game name");
        }
    }

    public void list() {
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
            GameData gameData = games.get(gameNumber - 1);
            int gameID = gameData.gameID();

            if (gameData.isOver()) {
                System.out.println("Game is closed");
                return;
            }

            String colorString = args[1];
            ChessGame.TeamColor color;
            if (colorString.equalsIgnoreCase("white")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (colorString.equalsIgnoreCase("black")) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                System.out.println("Color must be white or black");
                return;
            }

            setObservedGame(Integer.parseInt(args[0]) - 1);
            inGameMode = true;
            gameModeView = new GameModeManager(color, false);
            server.joinGame(gameID, color);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid game ID");
            inGameMode = false;
        } catch (Client.BadRequestResponse e) {
            System.out.println("Invalid game ID or color");
            inGameMode = false;
        } catch (Client.ForbiddenResponse e) {
            System.out.println("Position already filled");
            inGameMode = false;
        }
    }

    public void observe(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Must provide game ID");
            return;
        }

        try {
            int gameNumber = Integer.parseInt(args[0]) - 1;
            setObservedGame(gameNumber);
            int gameId = games.get(gameNumber).gameID();
            inGameMode = true;
            gameModeView = new GameModeManager(ChessGame.TeamColor.WHITE, true);
            server.observe(gameId);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Invalid game ID");
            inGameMode = false;
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
            server.makeMove(move, observedGame.gameID());
        } catch (InvalidMoveException e) {
            gameModeView.notify(e.getMessage());
        } catch (Exception e) {
            gameModeView.notify("Invalid move. Must provide a valid starting, ending position, " +
                    "and a promotion piece if applicable. (e.g., B3 A4 QUEEN)");
        }
    }

    private void highlightMoves(String[] args) {
        if (args.length == 0) {
            gameModeView.notify("Must provide a piece");
        }
        try {
            var pos = new ChessPosition(args[0]);
            gameModeView.renderGameWithHighlights(observedGame.game(), pos);
        } catch (Exception e) {
            gameModeView.notify("Invalid piece position");
        }
    }

    private void leaveGame() throws Exception {
        server.leaveGame(observedGame.gameID());
        gameModeView.exitGameMode();
        inGameMode = false;
    }

    private void resign() throws Exception {
        boolean confirm = gameModeView.confirmResign();
        if (confirm) {
            server.resign(observedGame.gameID());
        }
    }

    private void redraw() {
        gameModeView.renderGame(observedGame.game());
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

        GameData localGameData = games.get(gameNumber-1);

        // If the updated game is the observed game, re-render.
        if (localGameData.equals(observedGame)) {
            games.set(gameNumber-1, gameData);
            observedGame = gameData;
            gameModeView.renderGame(observedGame.game());
        }
    }

    public void handleAuthorization(String operation) throws Exception {
        if (operation.equals("login")
                || operation.equals("register")) {
            if (isAuthorized) {
                throw new Exception("Already logged in");
            }
        }
    }

    public void runCommand(String operation, String[] commandArgs) throws Exception {
        if (inGameMode) {
            switch (operation) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "highlight" -> highlightMoves(commandArgs);
                case "move" -> makeMove(commandArgs);
                case "leave" -> leaveGame();
                case "resign" -> resign();
                case "quit" -> quit();
                default -> gameModeView.notify("Not a valid command");
            }
        } else if (isAuthorized) {
            switch (operation) {
                case "help" -> help();
                case "list" -> list();
                case "create" -> create(commandArgs);
                case "join" -> join(commandArgs);
                case "observe" -> observe(commandArgs);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> System.out.println("Not a valid command");
            }
        } else {
            switch (operation) {
                case "help" -> help();
                case "register" -> register(commandArgs);
                case "login" -> login(commandArgs);
                case "quit" -> quit();
                default -> System.out.println("Not a valid command.");
            }
        }
    }

    public boolean processCommand(String command) {
        String[] commandArray = command.split("\\s+");
        if (commandArray.length == 0) {
            return true;
        }
        String operation = commandArray[0].toLowerCase(Locale.ROOT);
        String[] commandArgs = Arrays.copyOfRange(commandArray, 1, commandArray.length);

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
            runCommand(operation, commandArgs);
        } catch (Exception e) {
            System.out.println("An error occurred and the operation was unsuccessful.");
        }

        if (!inGameMode) {
            System.out.print("\n>>> ");
            System.out.flush();
        }

        return !command.equals("quit");
    }
}
