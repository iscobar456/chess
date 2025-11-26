package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
    ChessGame observedGame;
    boolean inGameMode;

    public CLI() {
        scanner = new Scanner(System.in);
        server = new ServerFacade("http", "localhost", 8080);
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
        if (args.length == 0) {
            System.out.println("Must provide game ID");
            return;
        }

        try {
            setObservedGame(Integer.parseInt(args[0]) - 1);
            renderGame();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid game ID");
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID");
        }
    }



    private void makeMove(String[] args) {
        if (args.length < 2) {
            System.out.println("Must provide a starting and ending position. (e.g., B2 C2)");
            return;
        }

        try {
            ChessPosition start = new ChessPosition(args[0]);
            ChessPosition end = new ChessPosition(args[1]);
            ChessPiece.PieceType type = null;
            if (args.length > 2) {
                type = ChessPiece.stringToType(args[3]);
            }
            ChessMove move = new ChessMove(start, end, type);
            observedGame.makeMove(move);
        } catch (Exception e) {
            System.out.println("Must provide a starting, ending position, " +
                    "and a promotion piece if applicable. (e.g., B3 A4 QUEEN)");
        }
    }

    private void renderGame() {
        BoardView view = new BoardView(observedGame, ChessGame.TeamColor.WHITE);
        System.out.print(view.render());
    }

    private void setObservedGame(int id) throws IndexOutOfBoundsException {
        observedGame = games.get(id).game();
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
        return handlerMap;
    }

    public boolean processCommand(String command) {
        try {
            String[] commandArray = command.split("\\s+");
            if (commandArray.length == 0) {
                return true;
            }

            String operation = commandArray[0];
            if (!handlers.containsKey(operation)) {
                System.out.println("Not a valid command");
                return true;
            }

            try {
                handleAuthorization(operation);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if (operation.equalsIgnoreCase("create")
                    || operation.equalsIgnoreCase("list")
                    || operation.equalsIgnoreCase("join")
                    || operation.equalsIgnoreCase("observe")) {
                games = server.getGames();
            }

            handlers.get(operation).run(Arrays.copyOfRange(commandArray, 1, commandArray.length));

            if (operation.equalsIgnoreCase("join")
                    || operation.equalsIgnoreCase("observe")) {

            }

        } catch (Exception e) {
            System.out.println("An error occurred and the operation was unsuccessful.");
        }

        return !command.equals("quit");
    }
}
